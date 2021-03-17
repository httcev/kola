import { Injectable } from '@angular/core';
import { Platform } from '@ionic/angular';
import { SQLiteObject } from '@ionic-native/sqlite/ngx';
import { AppVersion } from '@ionic-native/app-version/ngx';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { throwError } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { v4 as uuidv4 } from 'uuid';

import { NetworkService } from './network.service';
import { AssetsService, Asset } from './assets.service';
import { ErrorHandlerService } from './error-handler.service';
import { SettingsService } from './settings.service';
import { default as schema } from './database.schema.json';
import { encodeAsFilename } from '../util/base64';

interface Window {
  sqlitePlugin: any;
  openDatabase: any;
}
declare var window : Window;

export interface SyncResult {
  syncOK: boolean,
  codeStr: string,
  message: string,
  nbSent: number,
  nbUpdated: number,
  status?: number,
  localDataUpdated?: boolean,
  serverAnswer?: any
}

@Injectable({
  providedIn: 'root'
})
export class DatabaseService {
  db: SQLiteObject = null;
  tablesToSync = [];
  timeout: number = 20000;
  debugSql: boolean = false;
  cache: {} = {};
  preventSync = false;

  public syncing: BehaviorSubject<boolean> = new BehaviorSubject(false);
  public initialized: BehaviorSubject<boolean> = new BehaviorSubject(false);

  constructor(
    private networkService: NetworkService,
    private assetsService: AssetsService,
    private errorHandlerService: ErrorHandlerService,
    private settingsService: SettingsService,
    private platform: Platform,
    private httpClient: HttpClient,
    private appVersion: AppVersion
  ) {
    this.platform.ready().then(() => {
      this.networkService.online.subscribe(() => { this.sync() });
      this.settingsService.settings.subscribe(() => {
        this.initDatabase().then(() => { this.sync() }).catch((error) => {
          // prevent showing error when app is initially started
          if ('invalid_settings' !== error) {
            this.errorHandlerService.handle('Datenbank konnte nicht geöffnet werden.');
          }
        });
      });
    });
  }

  private async initDatabase(): Promise<any> {
    this.initialized.next(false);
    if (this.db != null && typeof this.db.close === 'function') {
      this.db.close();
      this.db = null;
    }
    if (this.settingsService.isValid()) {
      let settings = this.settingsService.getCurrentSettings();
      let dbName = encodeAsFilename(settings.url + ":" + settings.user) + ".db";
      if (this.platform.is('cordova')) {
        window.openDatabase = function(dbname, ignored1, ignored2, ignored3) {
          return window.sqlitePlugin.openDatabase({name: dbname, location: 'default'});
        };
      }
      this.db = window.openDatabase(dbName, '1', 'KOLA DB', 1024 * 1024 * 100);
      return this.initTables().then(() => { this.initialized.next(true); });
    }
    return Promise.reject("invalid_settings");
  }

  private async initTables(): Promise<any> {
    return new Promise(resolve => {
      this.db.transaction((tx) => {
        let promises = [];
        for (var tableName in schema) {
          if (Object.prototype.hasOwnProperty.call(schema, tableName)) {
            let tableDef = schema[tableName];
            var idType = tableDef.idType ? tableDef.idType : "VARCHAR(255)"
            var sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id " + idType + " PRIMARY KEY NOT NULL, ";
            for (var field in tableDef.extract) {
              sql += field + " " + tableDef.extract[field] + ", ";
            }
            sql += "modified BOOL, doc TEXT NOT NULL)";
            promises.push(this._executeSql(sql, [], tx));

            if (tableDef.sync) {
              this.tablesToSync.push({ 'tableName': tableName });
            }
          }
        }

        //create new table to store modified or new elems
        promises.push(this._executeSql('CREATE TABLE IF NOT EXISTS new_elem (table_name TEXT NOT NULL, id TEXT NOT NULL);', [], tx));
        promises.push(this._executeSql('CREATE INDEX IF NOT EXISTS index_tableName_newElem on new_elem (table_name)', [], tx));
        promises.push(this._executeSql('CREATE TABLE IF NOT EXISTS sync_info (last_sync TIMESTAMP);', [], tx));

        //create triggers to automatically fill the new_elem table (this table will contains a pointer to all the modified data)
        for (var table of this.tablesToSync) {
          promises.push(this._executeSql('CREATE TRIGGER IF NOT EXISTS update_' + table.tableName + '  AFTER UPDATE ON ' + table.tableName + ' ' +
            'BEGIN INSERT INTO new_elem (table_name, id) VALUES ("' + table.tableName + '", new.id); END;', [], tx));

          promises.push(this._executeSql('CREATE TRIGGER IF NOT EXISTS insert_' + table.tableName + '  AFTER INSERT ON ' + table.tableName + ' ' +
            'BEGIN INSERT INTO new_elem (table_name, id) VALUES ("' + table.tableName + '", new.id); END;', [], tx));
        }

        promises.push(this._selectSql('SELECT last_sync FROM sync_info').then(data => {
          if (data.length === 0 || data[0] == 0) {
            promises.push(this._executeSql('INSERT OR REPLACE INTO sync_info (last_sync) VALUES (0)', []));
          }
        }));
        Promise.all(promises).then(resolve);
      });
    });
  }

  public async sync(): Promise<SyncResult> {
    let syncResult: SyncResult = {
      syncOK: false,
      codeStr: 'noSync',
      message: 'No Sync yet',
      nbSent: 0,
      nbUpdated: 0
    };
    if (this.initialized.getValue()) {
      if (this.networkService.isOnline() && !this.preventSync) {
        this.syncing.next(true);
        try {
          this.preventSync = true;
          let clientData = await this._getDataToBackup(syncResult);
          let serverData = await this._sendDataToServer(clientData);
          if (!serverData || serverData.result === 'ERROR') {
            syncResult.syncOK = false;
            syncResult.codeStr = 'syncKoServer';
            if (serverData) {
              syncResult.status = serverData.status;
              syncResult.message = serverData.message;
            } else {
              syncResult.message = 'No answer from the server';
            }
            return syncResult;
          }

          try {
            await this.assetsService.uploadAttachments(clientData.data.asset);
          } catch (error) {
            this.errorHandlerService.handle("Fehler beim Hochladen von Anhängen");
          }
          try {
            await this.assetsService.downloadAttachments(serverData.data ? serverData.data.asset : []);
          } catch (error) {
            this.errorHandlerService.handle("Fehler beim Herunterladen von Anhängen");
          }
          await this._updateLocalDb(serverData, clientData, syncResult);

          syncResult.localDataUpdated = syncResult.nbUpdated > 0;
          syncResult.syncOK = true;
          syncResult.codeStr = 'syncOk';
          syncResult.message = 'Data synchronized successfully. (' + syncResult.nbSent + ' new/modified element saved, ' + syncResult.nbUpdated + ' updated)';
          syncResult.serverAnswer = serverData; //include the original server answer, just in case
        } finally {
          this.preventSync = false;
          this.syncing.next(false);
        }
      }
    }
    return syncResult;
  }

  public async getLastSyncDate(): Promise<string> {
    let lastSyncDate = '';
    let data = await this._selectSql('SELECT last_sync FROM sync_info');
    if (data.length > 0 && data[0].last_sync) {
      lastSyncDate = data[0].last_sync
    }
    return lastSyncDate;
  }

  private async _getDataToBackup(syncResult: SyncResult): Promise<any> {
    let lastSyncDate = await this.getLastSyncDate();
    let version = this.platform.is('cordova') ? await this.appVersion.getVersionNumber() : 'dummy';

    return new Promise<any>(resolve => {
      let dataToSync = {
        info: { 'lastSyncDate': lastSyncDate, 'version': version },
        data: {}
      };

      this.db.transaction(tx => {
        let promises = [];
        let nbData = 0;
        for (let index in this.tablesToSync) {
          let currTable = this.tablesToSync[index];
          promises.push(this._getDataToSave(currTable.tableName, tx).then(data => {
            dataToSync.data[currTable.tableName] = data;
            nbData += data.length;
          }));
        }
        Promise.all(promises).then(() => {
          syncResult.nbSent = nbData;
          resolve(dataToSync);
        });
      });
    });
  }

  _getDataToSave(tableName, tx): Promise<any> {
    let  sql = 'SELECT * FROM ' + tableName + ' WHERE id IN (SELECT DISTINCT id FROM new_elem WHERE table_name="' + tableName + '")';
    return this._selectSql(sql, tx);
  }

  async _sendDataToServer(dataToSync): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      var serverUrl = this.settingsService.getCurrentSettings().url + '/api/changes';
      this.httpClient.post(serverUrl, dataToSync).pipe(
        timeout(this.timeout),
      ).subscribe((data) => {
        resolve(data);
      }, (error) => {
        this.errorHandlerService.handle(error);
        reject();
      });
    });
  }

  async _updateLocalDb(serverData, clientData, syncResult: SyncResult): Promise<any> {
    if (typeof serverData.data === 'undefined' || serverData.data.length === 0) {
      //nothing to update
      //We only use the server date to avoid dealing with wrong date from the client
      return this._finishSync(serverData, clientData);
    }

    var counterNbTable = 0;
    var nbTables = this.tablesToSync.length;
    var counterNbElm = 0;

    for (let table of this.tablesToSync) {
      let currData = serverData.data[table.tableName];
      if (!currData) {
        //Should always be defined (even if 0 elements)
        //Must not be null
        currData = [];
      }
      for (let updatedObject of currData) {
        updatedObject.doc._table = table.tableName;
        await this.save(updatedObject.doc, true);
        counterNbElm++;
      }
    }

    syncResult.nbUpdated = counterNbElm;
    await this._finishSync(serverData, clientData);
  }


  async _getIdExistingInDB(tableName, listIdToCheck, tx): Promise<any[]> {
    if (listIdToCheck.length === 0) {
      return [];
    }
    let sql = 'SELECT id FROM ' + tableName + ' WHERE id IN ("' + this._arrayToString(listIdToCheck, '","') + '")';
    let ids = await this._selectSql(sql, tx);
    var idsInDb = [];
    for (var i = 0; i < ids.length; ++i) {
      idsInDb[ids[i]['id']] = true;
    }
    return idsInDb;
  }

  async _finishSync(serverData, clientData): Promise<any> {
    return new Promise(resolve => {
      let syncDate = serverData.now;
      let tableName, idsToDelete, i, idValue, idsString;
      let serverUpdatedAcks = serverData.updated || [];
      this.db.transaction(tx => {
        let promises = [];
        promises.push(this._executeSql('UPDATE sync_info SET last_sync = "' + syncDate + '"', [], tx));

        // Remove only the elem sent to the server (in case new_elem has been added during the sync)
        // We don't do that anymore: this._executeSql('DELETE FROM new_elem', [], tx);
        for (tableName in clientData.data) {
          idsToDelete = new Array();
          for (i = 0; i < clientData.data[tableName].length; i++) {
            idValue = clientData.data[tableName][i]['id'];
            // server now sends acks in "updated" id array. so only remove ids if they have successfully been updated by the server.
            if (serverUpdatedAcks.indexOf(idValue) > -1) {
              idsToDelete.push('"' + idValue + '"');
            } else {
              console.log("RETAINING " + idValue + ", because server didn't send ack.");
            }
          }
          if (idsToDelete.length > 0) {
            idsString = this._arrayToString(idsToDelete, ',');
            promises.push(this._executeSql('DELETE FROM new_elem WHERE table_name = "' + tableName + '" AND id IN (' + idsString + ')', [], tx));
          }
        }
        // Remove elems received from the server that has triggered the SQL TRIGGERS, to avoid to send it again to the server and create a loop
        for (tableName in serverData.data) {
          idsToDelete = new Array();
          for (i = 0; i < serverData.data[tableName].length; i++) {
            idValue = serverData.data[tableName][i]['id'];
            idsToDelete.push('"' + idValue + '"');
          }
          if (idsToDelete.length > 0) {
            idsString = this._arrayToString(idsToDelete, ',');
            promises.push(this._executeSql('DELETE FROM new_elem WHERE table_name = "' + tableName + '" AND id IN (' + idsString + ')', [], tx));
          }
        }
        Promise.all(promises).then(resolve);
      });
    });
  }

  private async _selectSql(sql, optionalTransaction?): Promise<any> {
    return new Promise((resolve, reject) => {
      this._executeSql(sql, [], optionalTransaction, (tx, rs) => {
        resolve(this._transformRs(rs));
      });
    });
  }

  private _transformRs(rs) {
    var elms = [];
    if (typeof(rs.rows) === 'undefined') {
      return elms;
    }

    for (var i = 0; i < rs.rows.length; ++i) {
      elms.push(rs.rows.item(i));
    }
    return elms;
  }

  private async _executeSql(sql: string, params, optionalTransaction?, optionalCallBack?): Promise<any> {
    if (this.debugSql) {
      console.log('_executeSql: ' + sql + ' with param ' + params);
    }
    if (!optionalCallBack) {
      optionalCallBack = this._defaultCallBack;
    }
    return new Promise(resolve => {
      if (optionalTransaction) {
        optionalTransaction.executeSql(sql, params, optionalCallBack, this._errorHandler);
        resolve();
      } else {
        this.db.transaction(tx => {
          tx.executeSql(sql, params, optionalCallBack, this._errorHandler);
          resolve();
        });
      }
    });
  }

  _arrayToString(array, separator) {
    var result = '';
    for (var i = 0; i < array.length; i++) {
      result += array[i];
      if (i < array.length - 1) {
        result += separator;
      }
    }
    return result;
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // return an observable with a user-facing error message
    return throwError('Something bad happened; please try again later.');
  }

  _defaultCallBack(transaction, results) {
    //console.log('SQL Query executed. insertId: '+results.insertId+' rows.length '+results.rows.length);
  }

  _errorHandler(transaction, error) {
    console.error('Error : ' + error.message + ' (Code ' + error.code + ') Transaction.sql = ' + transaction.sql);
  }
/*
  _buildInsertSQL(tableName, objToInsert, members) {
    if (members.length === 0) {
      throw 'buildInsertSQL : Error, try to insert an empty object in the table ' + tableName;
    }
    //build INSERT INTO myTable (attName1, attName2) VALUES (?, ?) -> need to pass the values in parameters
    var sql = 'INSERT INTO ' + tableName + ' (';
    sql += this._arrayToString(members, ',');
    sql += ') VALUES (';
    sql += this._getNbValString(members.length, '?', ',');
    sql += ')';
    return sql;
  }

  _buildUpdateSQL(tableName, objToUpdate, members) {
    var self = this;
    var sql = 'UPDATE ' + tableName + ' SET ';
    if (members.length === 0) {
      throw 'buildUpdateSQL : Error, try to insert an empty object in the table ' + tableName;
    }
    var nb = members.length;
    for (var i = 0; i < nb; i++) {
      sql += '"' + members[i] + '" = ?';
      if (i < nb - 1) {
        sql += ', ';
      }
    }

    return sql;
  }

  _getMembersValue(obj, members) {
    var memberArray = [];
    var val;
    for (var i = 0; i < members.length; i++) {
      val = obj[members[i]];
      if (val instanceof Object) {
        val = JSON.stringify(val);
      }
      memberArray.push(val);
    }
    return memberArray;
  }

  _getAttributesList(obj, check) {
    var memberArray = [];
    for (var elm in obj) {
      if (check && typeof this[elm] === 'function' && !obj.hasOwnProperty(elm)) {
        continue;
      }
      memberArray.push(elm);
    }
    return memberArray;
  }

  _getNbValString(nb, val, separator) {
    var result = '';
    for (var i = 0; i < nb; i++) {
      result += val;
      if (i < nb - 1) {
        result += separator;
      }
    }
    return result;
  }

  _getMembersValueString(obj, members, separator) {
    var result = '';
    for (var i = 0; i < members.length; i++) {
      result += '"' + obj[members[i]] + '"';
      if (i < members.length - 1) {
        result += separator;
      }
    }
    return result;
  }
  */

  // ---------------------------------------------------------------------------------------------------------------------------------------------------




  private _create(tableName:string, props?):{} {
    props = (typeof props === 'object' && props !== null) ? props : {};
    props['id'] = uuidv4();
    props['creator'] = this.settingsService.getCurrentSettings().userId;
    props['_table'] = tableName;
    props['_isNew'] = true;
    props['_modified'] = true;
    return props;
  }

  public async createTask() {
    var task = this._create("task", { isTemplate: false, reflectionQuestions: [] });
    // auto link standard reflection questions
    return this.all("reflectionQuestion").then(reflectionQuestions => {
      reflectionQuestions.forEach(reflectionQuestion => {
        if (reflectionQuestion.autoLink) {
          task['reflectionQuestions'].push(reflectionQuestion);
        }
      });
      return task;
    });
  }

  public createAttachment(parent): Asset {
    var attachment = this._create("asset", { typeLabel: "attachment" });
    parent.attachments = parent.attachments || [];
    parent.attachments.push(attachment);
    return attachment;
  }

  public createTaskDocumentation(taskId: string) {
    return this._create("taskDocumentation", { 'reference': taskId });
  }

  public createReflectionAnswer(taskId: string, questionId: string) {
    return this._create("reflectionAnswer", { task: taskId, question: questionId });
  }

  public createQuestion(referenceId?: string) {
    return this._create("question", { reference: referenceId, attachments:[] });
  }

  public createAnswer(questionId: string) {
    return this._create("answer", { question: questionId });
  }

  public createComment(targetId: string) {
    return this._create("comment", { reference: targetId });
  }

  public async save(doc, isUpdateFromServer: boolean = false) {
    return new Promise((resolve, reject) => {
      this.db.transaction(tx => {
        var promises = [];
        if (doc instanceof Array) {
          doc.forEach(o => {
            promises.push(this._save(o, tx, isUpdateFromServer));
          });
        } else {
          promises.push(this._save(doc, tx, isUpdateFromServer));
        }
        Promise.all(promises).then(() => {
          resolve();
          if (!isUpdateFromServer) {
            this.sync();
          }
        }, err => { this.errorHandlerService.handle(err); })
      });
    });
  }

  public async get(id, tableName:string, resolveReferences?:boolean, cache?:boolean) {
    //await this.timeout2(3000);
    if (cache === true && this.cache[tableName] && this.cache[tableName][id]) {
      return this.cache[tableName][id];
    }
    let doc = await this._get(id, tableName);
    if (resolveReferences !== false) {
      await this.resolveIds(doc);
    }
    if (cache === true) {
      this.cache[tableName] = this.cache[tableName] || {};
      this.cache[tableName][id] = doc;
    }
    return doc;
  }
/*
  private timeout2(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
  }
*/
  public async all(tableName:string, resolveReferences?:boolean, whereClause?:string): Promise<any> {
    //await this.timeout2(3000);
    return new Promise((resolve, reject) => {
      this.db.transaction(t => {
        var promises = [];
        var docs = [];

        var sql = "select doc,modified from " + tableName + " where ";
        if (whereClause) {
          sql += whereClause + " and ";
        }
        sql += "deleted <> 'true'";
        if (this.debugSql) {
          console.log(sql);
        }
        t.executeSql(sql, [], (tx, results) => {
          for (var i = 0; i < results.rows.length; i++) {
            var doc = JSON.parse(results.rows.item(i).doc);
            doc._table = tableName;
            doc._modified = (results.rows.item(i).modified === "true");
            docs.push(doc);
            if (resolveReferences) {
              promises.push(this._attachOneToMany(doc, tx).then(doc => {
                return this.resolveIds(doc);
              }));
            }
          }
          Promise.all(promises).then(() => {
            resolve(docs);
          });
        }, (tx, err) => {
          console.log(err);
          reject(err);
        });
      });
    });
  }

  public canEdit(doc:{}): boolean {
    if (doc !== null && typeof doc === 'object') {
      let currentUserId = this.settingsService.getCurrentSettings().userId;
      if (!isNaN(doc['creator'])) {
        return doc['creator'] === currentUserId;
      } else if (typeof doc['creator'] === 'object' && !isNaN(doc['creator']['id'])) {
        return doc['creator']['id'] === currentUserId;
      }
    }
    return false;
  }

  private async _save(doc, tx, isUpdateFromServer): Promise<any> {
    return new Promise((resolve, reject) => {
      if (!doc._table) {
        reject("Object has no _table property");
      } else {
        if (!doc.id) {
          doc.id = uuidv4();
        }
        let copy = this.deepCopy(doc);
        // delete local properties (all properties beginning with an underscore)
        for (var property in copy) {
          if (property.indexOf("_") === 0) {
            delete copy[property];
          }
        }

        let tableSchema = schema[doc._table];
        this._replaceIds(copy, tableSchema);

        let sql = "INSERT OR REPLACE INTO " + doc._table + " (id, modified, doc";
        let sqlValues = "?, ?, ?";
        let values = [copy.id, isUpdateFromServer !== true, JSON.stringify(copy)];
        for (let field in tableSchema.extract) {
          sql += ", " + field;
          sqlValues += ", ?";
          values.push(copy[field] || false);
        }
        sql += ") values (" + sqlValues + ")";
        if (this.debugSql) {
          console.log(sql, values);
        }

        if (this.cache[doc._table]) {
          delete this.cache[doc._table][doc.id];
        }

        tx.executeSql(sql, values, (tx, results) => {
          if (false && doc._table == "asset" && copy.typeLabel == "attachment") {
            //this.assetsService.copyToAssetsFolder(doc).then(() => { resolve() }, err => { reject(err) });
          } else {
            resolve();
          }
        }, (err) => {
          reject(err);
        });
      }
    });
  }

  private async _get(id, tableName): Promise<any> {
    return new Promise((resolve, reject) => {
      this.db.transaction(tx => {
        var sql = "select doc,modified from " + tableName + " where id=?";
        if (this.debugSql) {
          console.log(sql, [id]);
        }
        tx.executeSql(sql, [id], (tx, results) => {
          if (results.rows.length == 1) {
            var result = results.rows.item(0);
            var doc = JSON.parse(result.doc);
            doc._table = tableName;
            doc._modified = (result.modified === "true");
            // attach oneToMany relations
            this._attachOneToMany(doc, tx).then(() => {
              if (tableName == "asset" && doc.typeLabel == "attachment") {
                this.assetsService.appendLocalUrls(doc).then(() => {
                  resolve(doc);
                }, (err) => {
                  console.log(err);
                  reject(err);
                });
              } else {
                resolve(doc);
              }
            });
          } else {
            reject("no document with id '" + id + "' in table '" + tableName + "'");
          }
        }, (tx, err) => {
          console.log(err);
          reject(err);
        });
      });
    });
  }

  private async _attachOneToMany(doc, tx) {
    var promises = [];
    for (let index in schema[doc._table].joins) {
      let join = schema[doc._table].joins[index];
      doc[join.field] = [];
      var sql = "select id from " + join.targetTable + " where " + join.targetField + "=? and deleted <> 'true'";
      if (this.debugSql) {
        console.log(sql, [doc.id]);
      }
      promises.push(new Promise((resolve, reject) => {
        tx.executeSql(sql, [doc.id], (tx, results) => {
          for (var i = 0; i < results.rows.length; i++) {
            doc[join.field].push(results.rows.item(i).id);
          }
          resolve();
        }, (err) => {
          reject(err);
        });
      }));
    }
    await Promise.all(promises);
    return doc;
  }

  private async resolveIds(target): Promise<any> {
    var promises = [];
    for (let field in schema[target._table].references) {
      let targetTable = schema[target._table].references[field];
      var ids = target[field];
      if (ids != null) {
        if (ids instanceof Array) {
          ids.forEach((id, index) => {
            promises.push(this._get(id, targetTable).then(doc => {
              target[field][index] = doc;
              return this.resolveIds(doc);
            }));
          });
        } else {
          promises.push(this._get(ids, targetTable).then(doc => {
            target[field] = doc;
            return this.resolveIds(doc);
          }));
        }
      }
    }
    try {
      await Promise.all(promises);
    } catch(err) {
      this.errorHandlerService.handle(err);
    }
    return target;
  }

  private _replaceIds(target, tableSchema) {
    for (let field in tableSchema.references) {
      var values = target[field];
      if (values != null) {
        if (values instanceof Array) {
          values.forEach((value, index) => {
            if (value.id) {
              target[field][index] = value.id;
            }
          });
        } else {
          if (values.id) {
            target[field] = values.id;
          }
        }
      }
    }
  }

  private deepCopy(obj) {
    var copy;

    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;

    // Handle Date
    if (obj instanceof Date) {
        copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }

    // Handle Array
    if (obj instanceof Array) {
        copy = [];
        for (var i = 0, len = obj.length; i < len; i++) {
            copy[i] = this.deepCopy(obj[i]);
        }
        return copy;
    }

    // Handle Object
    if (obj instanceof Object) {
        copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = this.deepCopy(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
  }
}
