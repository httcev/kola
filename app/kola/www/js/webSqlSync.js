/*******************************************************************
 * Sync a local WebSQL DB (SQLite) with a server.
 * Thanks to Lee Barney and QuickConnect for his inspiration
 ******************************************************************/
/*
 Copyright (c) 2012, Samuel Michelot,  MosaCrea Ltd
 Permission is hereby granted, free of charge, to any person obtaining a
 copy of this software and associated documentation files (the "Software"),
 to deal in the Software without restriction, including without limitation the
 rights to use, copy, modify, merge, publish, distribute, sublicense,
 and/or sell copies of the Software, and to permit persons to whom the Software
 is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.


 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

(function(root, factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define(factory);
	} else {
		// Browser globals
		root.DBSYNC = factory();
	}
}(this, function() {

	var DBSYNC = {
		serverUrl: null,
		db: null,
		tablesToSync: [], //eg.  [{tableName : 'myDbTable', idName : 'myTable_id'},{tableName : 'stat'}]
		idNameFromTableName: {}, //map to get the idName with the tableName (key)
		syncInfo: { //this object can have other useful info for the server ex. {deviceId : "XXXX", email : "fake@g.com"}
			lastSyncDate: null // attribute managed by webSqlSync
		},
		syncResult: null,
		firstSync: false,
		cbEndSync: null,
		clientData: null,
		serverData: null,

		authenticationService: null, // basic authentication support

		/*************** PUBLIC FUNCTIONS ********************/
		/**
		 * Initialize the synchronization (should be called before any call to syncNow)
		 * (it will create automatically the necessary tables and triggers if needed)
		 * @param {Object} theTablesToSync : ex : [{ tableName: 'card_stat', idName: 'card_id'}, {tableName: 'stat'}] //no need to precise id if the idName is "id".
		 * @param {Object} dbObject : the WebSQL database object.
		 * @param {Object} theSyncInfo : will be sent to the server (useful to store any ID or device info).
		 * @param {Object} theServerUrl
		 * @param {Object} callBack(firstInit) : called when init finished.
		 * @param {Object} authenticationService : credentials provider for basic authentication support
		 * @param {Object} timeout : the timeout in milliseconds for the ajax request making the sync
		 */
		initSync: function(dbService, schema, dbObject, theSyncInfo, theServerUrl, attachmentUploadUrl, assetsDir, callBack, $cordovaFileTransfer, $cordovaFile, $q, authenticationService, timeout) {
			var self = this,
				i = 0;
			this.db = dbObject;
			this.dbService = dbService;
			this.serverUrl = theServerUrl;
			this.tablesToSync = [];
			this.syncInfo = theSyncInfo;
			this.authenticationService = authenticationService;
			this.timeout = timeout;
			this.$cordovaFileTransfer = $cordovaFileTransfer;
			this.$cordovaFile = $cordovaFile;
			this.attachmentUploadUrl = attachmentUploadUrl;
			this.assetsDir = assetsDir;
			this.$q = $q;

			angular.forEach(schema, function(tableDef, tableName) {
				if (tableDef.sync) {
					self.tablesToSync.push({
						tableName: tableName
					});
				}
			})

			//Handle optional id :
			for (i = 0; i < self.tablesToSync.length; i++) {
				if (typeof self.tablesToSync[i].idName === 'undefined') {
					self.tablesToSync[i].idName = 'id'; //if not specified, the default name is 'id'
				}
				self.idNameFromTableName[self.tablesToSync[i].tableName] = self.tablesToSync[i].idName;
			}

			self.db.transaction(function(transaction) {
				//create new table to store modified or new elems
				self._executeSql('CREATE TABLE IF NOT EXISTS new_elem (table_name TEXT NOT NULL, id TEXT NOT NULL);', [], transaction);
				self._executeSql('CREATE INDEX IF NOT EXISTS index_tableName_newElem on new_elem (table_name)', [], transaction);
				self._executeSql('CREATE TABLE IF NOT EXISTS sync_info (last_sync TIMESTAMP);', [], transaction);

				//create triggers to automatically fill the new_elem table (this table will contains a pointer to all the modified data)
				for (i = 0; i < self.tablesToSync.length; i++) {
					var curr = self.tablesToSync[i];
					self._executeSql('CREATE TRIGGER IF NOT EXISTS update_' + curr.tableName + '  AFTER UPDATE ON ' + curr.tableName + ' ' +
						'BEGIN INSERT INTO new_elem (table_name, id) VALUES ("' + curr.tableName + '", new.' + curr.idName + '); END;', [], transaction);

					self._executeSql('CREATE TRIGGER IF NOT EXISTS insert_' + curr.tableName + '  AFTER INSERT ON ' + curr.tableName + ' ' +
						'BEGIN INSERT INTO new_elem (table_name, id) VALUES ("' + curr.tableName + '", new.' + curr.idName + '); END;', [], transaction);
					//TODO the DELETE is not handled. But it's not a pb if you do a logic delete (ex. update set state="DELETED")
				}
			}); //end tx
			self._selectSql('SELECT last_sync FROM sync_info', null, function(res) {
				if (res.length === 0 || res[0] == 0) { //First sync (or data lost)
					self._executeSql('INSERT OR REPLACE INTO sync_info (last_sync) VALUES (0)', []);
					self.firstSync = true;
					self.syncInfo.lastSyncDate = 0;
					callBack(true);
				} else {
					self.syncInfo.lastSyncDate = res[0].last_sync;
					if (self.syncInfo.lastSyncDate === 0) {
						self.firstSync = true;
					}
					callBack(false);
				}
			});
		},

		/**
		 *
		 * @param {function} callBackProgress
		 * @param {function} callBackEnd (result.syncOK, result.message).
		 * @param {boolean} saveBandwidth (default false): if true, the client will not send a request to the server if there is no local changes
		 */
		syncNow: function(callBackProgress, callBackEndSync, saveBandwidth) {
			var self = this;
			if (this.db === null) {
				throw 'You should call the initSync before (db is null)';
			}
			if (!callBackProgress) {
				callBackProgress = this.log;
			}
			if (!callBackEndSync) {
				callBackEndSync = this.log;
			}

			self.syncResult = {
				syncOK: false,
				codeStr: 'noSync',
				message: 'No Sync yet',
				nbSent: 0,
				nbUpdated: 0
			};

			self.cbEndSync = function() {
				callBackProgress(self.syncResult.message, 100, self.syncResult.codeStr);
				callBackEndSync(self.syncResult);
			};

			callBackProgress('Getting local data to backup', 0, 'getData');

			self._getDataToBackup(function(data) {
				self.clientData = data;
				if (saveBandwidth && self.syncResult.nbSent === 0) {
					self.syncResult.localDataUpdated = false;
					self.syncResult.syncOK = true;
					self.syncResult.codeStr = 'nothingToSend';
					self.syncResult.message = 'No new data to send to the server';
					self.cbEndSync(self.syncResult);
					return
				}

				callBackProgress('Sending ' + self.syncResult.nbSent + ' elements to the server', 20, 'sendData');

				self._sendDataToServer(data, function(serverData) {
					self._uploadAttachments(data.data.asset).finally(function() {
							callBackProgress('Updating local data', 70, 'updateData');
							//console.log("--- data", data);
							self._downloadAttachments(serverData.data ? serverData.data.asset : []).finally(function() {
								self._updateLocalDb(serverData, function() {
									self.syncResult.localDataUpdated = self.syncResult.nbUpdated > 0;
									self.syncResult.syncOK = true;
									self.syncResult.codeStr = 'syncOk';
									self.syncResult.message = 'Data synchronized successfully. (' + self.syncResult.nbSent +
										' new/modified element saved, ' + self.syncResult.nbUpdated + ' updated)';
									self.syncResult.serverAnswer = serverData; //include the original server answer, just in case
									self.cbEndSync(self.syncResult);
								});
							});
						}
						/*, function() {
						                    // uploading failed
						                    self.syncResult.syncOK = false;
						                    self.syncResult.codeStr = 'uploadFailed';
						                    self.syncResult.message = 'Uploading new assets failed';
						                    self.cbEndSync(self.syncResult);
						                }*/
					);
				}, function() {
					// Called when a timeout occurred
					self.syncResult.syncOK = false;
					self.syncResult.codeStr = 'timeout';
					self.syncResult.message = 'The server is unavailable, please try again later';
					self.cbEndSync(self.syncResult);
				});
			});

		},

		/* You can override the following methods to use your own log */
		log: function(message) {
			//console.log(message);
		},
		error: function(message) {
			console.error(message);
		},
		getLastSyncDate: function() {
			return this.syncInfo.lastSyncDate;
		},
		// Usefull to tell the server to resend all the data from a particular Date (val = 1 : the server will send all his data)
		setSyncDate: function(val) {
			this.syncInfo.lastSyncDate = val;
			this._executeSql('UPDATE sync_info SET last_sync = "' + this.syncInfo.lastSyncDate + '"', []);
		},
		//Useful to tell the client to send all his data again (like the firstSync)
		setFirstSync: function() {
			this.firstSync = true;
			this.syncInfo.lastSyncDate = 0;
			this._executeSql('UPDATE sync_info SET last_sync = "' + this.syncInfo.lastSyncDate + '"', []);
		},
		/*************** PRIVATE FUNCTIONS ********************/

		_getDataToBackup: function(callBack) {
			var self = this,
				nbData = 0;
			self.log('_getDataToBackup');
			var dataToSync = {
				info: self.syncInfo,
				data: {}
			};

			self.db.transaction(function(tx) {
				var i, counter = 0,
					nbTables = self.tablesToSync.length,
					currTable;

				self.tablesToSync.forEach(function(currTable) { //a simple for will not work here because we have an asynchronous call inside
					self._getDataToSave(currTable.tableName, currTable.idName, self.firstSync, tx, function(data) {
						dataToSync.data[currTable.tableName] = data;
						nbData += data.length;
						counter++;
						if (counter === nbTables) { //only call the callback at the last table
							self.log('Data fetched from the local DB');
							//dataToSync.info.nbDataToBackup = nbData;
							self.syncResult.nbSent = nbData;
							callBack(dataToSync);
						}
					});
				}); //end for each
			}); //end tx
		},

		_getDataToSave: function(tableName, idName, needAllData, tx, dataCallBack) {
			var self = this,
				sql = '';
			if (needAllData) {
				sql = 'SELECT * FROM ' + tableName;
			} else {
				sql = 'SELECT * FROM ' + tableName + ' WHERE ' + idName + ' IN (SELECT DISTINCT id FROM new_elem WHERE table_name="' + tableName + '")';
			}
			self._selectSql(sql, tx, dataCallBack);
		},


		_sendDataToServer: function(dataToSync, callBack, timeoutCallBack) {
			var self = this;
			var xhrBusy = true;
			var credentials = self.authenticationService.getCredentials();

			var XHR = new window.XMLHttpRequest(),
				data = JSON.stringify(dataToSync);
			XHR.overrideMimeType = 'application/json;charset=UTF-8';

			if (credentials.user != null && credentials.password != null && credentials.user != undefined && credentials.password != undefined) {
				XHR.open("POST", self.serverUrl, true);
				XHR.setRequestHeader("Authorization", "Basic " + self._encodeBase64(credentials.user + ':' + credentials.password));
			} else {
				XHR.open("POST", self.serverUrl, true);
			}

			XHR.setRequestHeader("Content-type", "application/json; charset=utf-8");
			XHR.onreadystatechange = function() {
				var serverAnswer;
				if (4 === XHR.readyState) {
					xhrBusy = false;
					try {
						serverAnswer = JSON.parse(XHR.responseText);
					} catch (e) {
						serverAnswer = XHR.responseText;
					}
					self.log('Server answered: ');
					self.log(serverAnswer);
					//I want only json/object as response
					if (XHR.status == 200 && serverAnswer instanceof Object) {
						callBack(serverAnswer);
					} else {
						serverAnswer = {
							result: 'ERROR',
							status: XHR.status,
							message: XHR.statusText
						};
						callBack(serverAnswer);
					}
				}
			};

			if (self.timeout > 0) {
				setTimeout(function() {
					if (xhrBusy === true) {
						XHR.abort();
						timeoutCallBack();
					}
				}, self.timeout);
			}

			XHR.send(data);

		},

		_updateLocalDb: function(serverData, callBack) {
			var self = this;
			self.serverData = serverData;

			if (!serverData || serverData.result === 'ERROR') {
				self.syncResult.syncOK = false;
				self.syncResult.codeStr = 'syncKoServer';
				if (serverData) {
					self.syncResult.status = serverData.status;
					self.syncResult.message = serverData.message;
				} else {
					self.syncResult.message = 'No answer from the server';
				}
				self.cbEndSync(self.syncResult);
				return;
			}
			if (typeof serverData.data === 'undefined' || serverData.data.length === 0) {
				//nothing to update
				//We only use the server date to avoid dealing with wrong date from the client
				self._finishSync(serverData.now, callBack);
				return;
			}
			//        self.db.transaction(function(tx) {
			var counterNbTable = 0,
				nbTables = self.tablesToSync.length;
			var counterNbElm = 0;
			var promises = [];
			self.tablesToSync.forEach(function(table) {
				var currData = serverData.data[table.tableName];
				if (!currData) {
					//Should always be defined (even if 0 elements)
					//Must not be null
					currData = [];
				}
				angular.forEach(currData, function(updatedObject) {
					updatedObject.doc._table = table.tableName;
					promises.push(self.dbService.save(updatedObject.doc, true).then(function() {
						counterNbElm++;
					}));
				});
			});
			self.$q.all(promises).then(function() {
				self.syncResult.nbUpdated = counterNbElm;
				self._finishSync(serverData.now, callBack);
			});

			/*
			                var nb = currData.length;
			                counterNbElm += nb;
			                self.log('There are ' + nb + ' new or modified elements in the table ' + table.tableName + ' to save in the local DB');

			                var i = 0, listIdToCheck = [];
			                for (i = 0; i < nb; i++) {
			                    listIdToCheck.push(serverData.data[table.tableName][i][table.idName]);
			                }

			                self._getIdExitingInDB(table.tableName, table.idName, listIdToCheck, tx, function(idInDb) {

			                    var curr = null, sql = null;

			                    for (i = 0; i < nb; i++) {

			                        curr = serverData.data[table.tableName][i];

			                        if (idInDb[curr[table.idName]]) {//update

			                            var attList = self._getAttributesList(curr);
			                            sql = self._buildUpdateSQL(table.tableName, curr, attList);
			                            sql += ' WHERE ' + table.idName + ' = "' + curr[table.idName] + '"';

			                            var attValue = self._getMembersValue(curr, attList);
			                            self._executeSql(sql, attValue, tx);

			                        } else {//insert
			                            //'ex INSERT INTO tablename (id, name, type, etc) VALUES (?, ?, ?, ?);'
			                            var attList = self._getAttributesList(curr);
			                            sql = self._buildInsertSQL(table.tableName, curr, attList);
			                            var attValue = self._getMembersValue(curr, attList);

			                            self._executeSql(sql, attValue, tx);
			                        }
			                    }//end for
			                    counterNbTable++;
			                    if (counterNbTable === nbTables) {
			                        //TODO set counterNbElm to info
			                        self.syncResult.nbUpdated = counterNbElm;
			                        self._finishSync(serverData.now, tx, callBack);
			                    }
			                });//end getExisting Id
			            });//end forEach
			        });//end tx
			        */
		},
		/** return the listIdToCheck curated from the id that doesn't exist in tableName and idName
		 * (used in the DBSync class to know if we need to insert new elem or just update)
		 * @param {Object} tableName : card_stat.
		 * @param {Object} idName : ex. card_id.
		 * @param {Object} listIdToCheck : ex. [10000, 10010].
		 * @param {Object} dataCallBack(listIdsExistingInDb[id] === true).
		 */
		_getIdExitingInDB: function(tableName, idName, listIdToCheck, tx, dataCallBack) {
			if (listIdToCheck.length === 0) {
				dataCallBack([]);
				return;
			}
			var self = this;
			var SQL = 'SELECT ' + idName + ' FROM ' + tableName + ' WHERE ' + idName + ' IN ("' + self._arrayToString(listIdToCheck, '","') + '")';
			self._selectSql(SQL, tx, function(ids) {
				var idsInDb = [];
				for (var i = 0; i < ids.length; ++i) {
					idsInDb[ids[i][idName]] = true;
				}
				dataCallBack(idsInDb);
			});
		},
		_finishSync: function(syncDate, callBack) {
			var self = this,
				tableName, idsToDelete, idName, i, idValue, idsString;
			var serverUpdatedAcks = self.serverData.updated || [];
			this.firstSync = false;
			this.syncInfo.lastSyncDate = syncDate;
			self.db.transaction(function(tx) {
				self._executeSql('UPDATE sync_info SET last_sync = "' + syncDate + '"', [], tx);

				// Remove only the elem sent to the server (in case new_elem has been added during the sync)
				// We don't do that anymore: this._executeSql('DELETE FROM new_elem', [], tx);
				for (tableName in self.clientData.data) {
					idsToDelete = new Array();
					idName = self.idNameFromTableName[tableName];
					for (i = 0; i < self.clientData.data[tableName].length; i++) {
						idValue = self.clientData.data[tableName][i][idName];
						// server now sends acks in "updated" id array. so only remove ids if they have successfully been updated by the server.
						if (serverUpdatedAcks.indexOf(idValue) > -1) {
							idsToDelete.push('"' + idValue + '"');
						} else {
							console.log(self.clientData);
							console.log("RETAINING " + idValue + ", because server didn't send ack.");
						}
					}
					if (idsToDelete.length > 0) {
						idsString = self._arrayToString(idsToDelete, ',');
						self._executeSql('DELETE FROM new_elem WHERE table_name = "' + tableName + '" AND id IN (' + idsString + ')', [], tx);
					}
				}
				// Remove elems received from the server that has triggered the SQL TRIGGERS, to avoid to send it again to the server and create a loop
				for (tableName in self.serverData.data) {
					idsToDelete = new Array();
					idName = self.idNameFromTableName[tableName];
					for (i = 0; i < self.serverData.data[tableName].length; i++) {
						idValue = self.serverData.data[tableName][i][idName];
						idsToDelete.push('"' + idValue + '"');
					}
					if (idsToDelete.length > 0) {
						idsString = self._arrayToString(idsToDelete, ',');
						self._executeSql('DELETE FROM new_elem WHERE table_name = "' + tableName + '" AND id IN (' + idsString + ')', [], tx);
					}
				}
				callBack();
				self.clientData = null;
				self.serverData = null;
			});
		},
		_uploadAttachments: function(attachments) {
			var self = this;
			var crendentials = self.authenticationService.getCredentials();
			var promises = [];
			if (ionic.Platform.isWebView()) {
				var options = {
					headers: {
						"Authorization": "Basic " + this._encodeBase64(crendentials.user + ':' + crendentials.password)
					}
				};
				angular.forEach(attachments, function(attachment) {
					var doc = JSON.parse(attachment.doc);
					if (doc.typeLabel == "attachment") {
						console.log("--- uploading " + (self.assetsDir + doc.id) + " to " + (self.attachmentUploadUrl + "/" + doc.id));
						promises.push(self.$cordovaFileTransfer.upload(self.attachmentUploadUrl + "/" + doc.id, self.assetsDir + doc.id, options)
							.then(function(result) {
								// Success!
							}, function(err) {
								// Error
								console.error("--- upload error", err);
								return self.$q.reject();
							}, function(progress) {
								// constant progress updates
								//console.log("--- upload progress", progress);
							}));
					}
				});
			}
			return this.$q.all(promises);
		},
		_downloadAttachments: function(attachments) {
			var self = this;
			var crendentials = self.authenticationService.getCredentials();
			var promises = [];
			if (ionic.Platform.isWebView()) {
				var options = {
					headers: {
						"Authorization": "Basic " + this._encodeBase64(crendentials.user + ':' + crendentials.password)
					}
				};
				angular.forEach(attachments, function(attachment) {
					var att = attachment.doc;
					if (att && att.typeLabel == "attachment") {
						// only download if not available locally already
						promises.push(self.$cordovaFile.checkFile(self.assetsDir, att.id)
							.then(function(success) {
								// success
								console.log("--- found file " + att.id + " -> NOT downloading.");
							}, function(error) {
								// file not found
								console.log("--- downloading attachment from " + (att.url) + " to " + (self.assetsDir + att.id));
								console.log(attachment);
								return self.$cordovaFileTransfer.download(att.url, self.assetsDir + att.id, options, false)
									.then(function(fileEntry) {
										// Success!
										console.log("--- success");
										console.log(fileEntry);
										/*
                            fileEntry.file(function(f) {
                              attachment.url = f.localURL;
                              d.resolve();
                            });
            */
									}, function(err) {
										// Error
										console.log("--- error");
										console.error(err);
									}, function(progress) {
										// console.log("--- progress: " +((progress.loaded / progress.total) * 100));
									});
							}));
					}
				});
			}
			return this.$q.all(promises);
		},


		/***************** DB  util ****************/

		_selectSql: function(sql, optionalTransaction, callBack) {
			var self = this;
			self._executeSql(sql, [], optionalTransaction, function(tx, rs) {
				callBack(self._transformRs(rs));
			}, self._errorHandler);
		},
		_transformRs: function(rs) {
			var elms = [];
			if (typeof(rs.rows) === 'undefined') {
				return elms;
			}

			for (var i = 0; i < rs.rows.length; ++i) {
				elms.push(rs.rows.item(i));
			}
			return elms;
		},

		_executeSql: function(sql, params, optionalTransaction, optionalCallBack) {
			var self = this;
			self.log('_executeSql: ' + sql + ' with param ' + params);
			if (!optionalCallBack) {
				optionalCallBack = self._defaultCallBack;
			}
			if (optionalTransaction) {
				self._executeSqlBridge(optionalTransaction, sql, params, optionalCallBack, self._errorHandler);
			} else {
				self.db.transaction(function(tx) {
					self._executeSqlBridge(tx, sql, params, optionalCallBack, self._errorHandler);
				});
			}
		},
		_executeSqlBridge: function(tx, sql, params, dataHandler, errorHandler) {
			var self = this;

			//Standard WebSQL
			tx.executeSql(sql, params, dataHandler, errorHandler);

		},

		_defaultCallBack: function(transaction, results) {
			//DBSYNC.log('SQL Query executed. insertId: '+results.insertId+' rows.length '+results.rows.length);
		},

		_errorHandler: function(transaction, error) {
			DBSYNC.error('Error : ' + error.message + ' (Code ' + error.code + ') Transaction.sql = ' + transaction.sql);
		},

		_buildInsertSQL: function(tableName, objToInsert, members) {
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
		},

		_buildUpdateSQL: function(tableName, objToUpdate, members) {
			/*ex UPDATE "nom de table" SET colonne 1 = [valeur 1], colonne 2 = [valeur 2] WHERE {condition}*/
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
		},
		_getMembersValue: function(obj, members) {
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
		},
		_getAttributesList: function(obj, check) {
			var memberArray = [];
			for (var elm in obj) {
				if (check && typeof this[elm] === 'function' && !obj.hasOwnProperty(elm)) {
					continue;
				}
				memberArray.push(elm);
			}
			return memberArray;
		},
		_getNbValString: function(nb, val, separator) {
			var result = '';
			for (var i = 0; i < nb; i++) {
				result += val;
				if (i < nb - 1) {
					result += separator;
				}
			}
			return result;
		},
		_getMembersValueString: function(obj, members, separator) {
			var result = '';
			for (var i = 0; i < members.length; i++) {
				result += '"' + obj[members[i]] + '"';
				if (i < members.length - 1) {
					result += separator;
				}
			}
			return result;
		},
		_arrayToString: function(array, separator) {
			var result = '';
			for (var i = 0; i < array.length; i++) {
				result += array[i];
				if (i < array.length - 1) {
					result += separator;
				}
			}
			return result;
		},

		_keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

		// public method for encoding
		_encodeBase64: function(input) {
			var output = "";
			var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
			var i = 0;

			input = this._utf8_encode(input);

			while (i < input.length) {

				chr1 = input.charCodeAt(i++);
				chr2 = input.charCodeAt(i++);
				chr3 = input.charCodeAt(i++);

				enc1 = chr1 >> 2;
				enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
				enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
				enc4 = chr3 & 63;

				if (isNaN(chr2)) {
					enc3 = enc4 = 64;
				} else if (isNaN(chr3)) {
					enc4 = 64;
				}

				output = output +
					this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
					this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

			}

			return output;
		},

		_utf8_encode: function(string) {
			string = string.replace(/\r\n/g, "\n");
			var utftext = "";

			for (var n = 0; n < string.length; n++) {

				var c = string.charCodeAt(n);

				if (c < 128) {
					utftext += String.fromCharCode(c);
				} else if ((c > 127) && (c < 2048)) {
					utftext += String.fromCharCode((c >> 6) | 192);
					utftext += String.fromCharCode((c & 63) | 128);
				} else {
					utftext += String.fromCharCode((c >> 12) | 224);
					utftext += String.fromCharCode(((c >> 6) & 63) | 128);
					utftext += String.fromCharCode((c & 63) | 128);
				}

			}

			return utftext;
		}
	};

	return DBSYNC;
}));
