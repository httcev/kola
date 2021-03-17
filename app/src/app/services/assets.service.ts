import { Injectable }  from '@angular/core';
import { Platform, ModalController } from '@ionic/angular';
import { File } from '@ionic-native/file/ngx';
import { WebView } from '@ionic-native/ionic-webview/ngx';
import { FileOpener } from '@ionic-native/file-opener/ngx';
import { HttpClient } from '@angular/common/http';

import { ErrorHandlerService } from './error-handler.service';
import { SettingsService, Settings } from './settings.service';
import { encodeBase64 } from '../util/base64';
import { AssetModalComponent } from '../components/asset-modal/asset-modal.component';

export interface Asset {
  id?: string;
  url?: string;
  doc?: any;
  typeLabel?: string;
  mimeType?: string;
  _internalURL?: string;
}

interface Window {
  resolveLocalFileSystemURL: any;
  open: any;
}
declare var window : Window;

@Injectable({
  providedIn: 'root'
})
export class AssetsService {
  cacheDirname: string;
  assetsDir: any;
  assetsDirname: string;

  constructor(
    private platform: Platform,
    private file: File,
    private httpClient: HttpClient,
    private webview: WebView,
    private fileOpener: FileOpener,
    private modalController: ModalController,
    private errorHandlerService: ErrorHandlerService,
    private settingsService: SettingsService
  ) {
    this.platform.ready().then(() => {
      if (this.platform.is('cordova')) {
        this.cacheDirname = this.file.externalCacheDirectory;
        let dataDirname = this.platform.is("android") ? this.file.externalDataDirectory : this.file.dataDirectory;
        this.assetsDirname = dataDirname + 'assets/';
        window.resolveLocalFileSystemURL(dataDirname, dataDir => {
          dataDir.getDirectory('assets', { create: true }, assetsDir => {
            this.assetsDir = assetsDir;
          }, () => {
            this.errorHandlerService.handle('Verzeichnis zum Speichern von Anhängen konnte nicht angelegt werden.');
          });
        });
      }
    });
  }

  public async uploadAttachments(attachments: Asset[]): Promise<any> {
    var promises = [];
    if (this.platform.is('cordova')) {
      let settings: Settings = this.settingsService.getCurrentSettings();
      let attachmentUploadUrl: string = settings.url + '/api/upload/';

      attachments.forEach(attachment => {
        var doc = JSON.parse(attachment.doc);
        if (doc.typeLabel === 'attachment') {
          promises.push(new Promise((resolve, reject) => {
            console.log("--- uploading " + (this.assetsDirname + doc.id) + " to " + attachmentUploadUrl + doc.id);
            this.assetsDir.getFile(attachment.id, { create: false, exclusive: false }, (fileEntry) => {
              fileEntry.file(file => {
                const reader = new FileReader();
                reader.onloadend = () => {
                  const blob = new Blob([reader.result]);
                  const data = new FormData();
                  data.append('file', blob);
                  this.httpClient.post(attachmentUploadUrl + doc.id, data).subscribe(() => {
                    resolve();
                  }, (error) => {
                    this.errorHandlerService.handle(error);
                    reject(error);
                  });
                };
                reader.readAsArrayBuffer(file);
              });
            }, (err) => { reject(err); });
          }));
        }
      });
    }
    return Promise.all(promises);
  }

  public async downloadAttachments(attachments: Asset[]): Promise<any> {
    var promises = [];
    if (this.platform.is('cordova')) {
      attachments.forEach(attachment => {
        var att = attachment.doc;
        if (att && att.typeLabel === 'attachment') {
          // only download if not available locally already
          promises.push(new Promise((resolve, reject) => {
            this.file.checkFile(this.assetsDirname, att.id).then(() => {
              // success
              console.log("--- found file " + att.id + " -> NOT downloading.")
              resolve();
            }).catch(error => {
              console.log("--- downloading attachment from " + (att.url) + " to " + (this.assetsDirname + att.id), att);
              this.httpClient.get(att.url, { responseType: 'blob', observe: 'response' }).subscribe(response => {
                this.assetsDir.getFile(attachment.id, { create: true, exclusive: false }, (fileEntry) => {
                  fileEntry.createWriter(fileWriter => {
                    fileWriter.onwriteend = function() {
                      resolve();
                    };

                    fileWriter.onerror = function (error) {
                      this.errorHandlerService.handle(error);
                      reject(error);
                    };

                    const blob = new Blob([response.body]);//, { type: 'image/png' });
                    fileWriter.write(blob);
                  });
                }, (error) => { reject(error); });
              }, (error) => {
                reject(error);
              });
            })
          }));
        }
      });
    }
    return Promise.all(promises);
  }

  public async copyToAssetsFolder(fileUrl: string, attachment: Asset): Promise<any> {
    return new Promise((resolve, reject) => {
      let id = attachment.id;
      console.log('--- copy from '+fileUrl+' to '+(this.assetsDirname + id));
      window.resolveLocalFileSystemURL(fileUrl, (fileEntry) => {
        // move attachment data to assets dir if it is not already there
        if (fileEntry.nativeURL !== (this.assetsDirname + id)) {
          // check if the asset is in the app's cache folder. if yes, move the asset, if not copy it (to avoid removing files selected from the photo library).
          if (fileEntry.nativeURL.indexOf(this.cacheDirname) === 0) {
            fileEntry.moveTo(this.assetsDir, id, () => {
              this.appendLocalUrls(attachment).then(() => resolve(), (err) => reject(err));
            }, (err) => { reject(err); });
          } else {
            fileEntry.copyTo(this.assetsDir, id, () => {
              this.appendLocalUrls(attachment).then(() => resolve(), (err) => reject(err));
            }, (err) => { reject(err); });
          }
        } else {
          // file is already in assets dir
          resolve();
        }
      }, (err) => { reject(err); });
    });
  }

  public async appendLocalUrls(attachment: Asset): Promise<any> {
    return new Promise((resolve, reject) => {
      if (this.platform.is('cordova') && attachment.typeLabel === 'attachment') {
        this.assetsDir.getFile(attachment.id, { create: false, exclusive: false }, (fileEntry) => {
          attachment.url = this.webview.convertFileSrc(fileEntry.toURL());
          attachment._internalURL = fileEntry.toInternalURL();
          resolve();
        }, () => {
          // file not found
          reject("Anhang konnte nicht geladen werden: " + attachment.id);
        });
      } else {
        resolve();
      }
    });
  }

  public async openAttachment(attachment: Asset): Promise<any> {
    if (attachment.mimeType.indexOf('image/') === 0) {
      const modal = await this.modalController.create({
        component: AssetModalComponent,
        componentProps: { 'asset': attachment}
      });
      return await modal.present();
    } else  {
      if (this.platform.is('cordova')) {
        if (attachment.typeLabel === 'attachment') {
          console.log("--- opening with url " + attachment._internalURL + ", mime=" + attachment.mimeType, attachment);
          this.fileOpener.showOpenWithDialog(attachment._internalURL, attachment.mimeType)
          .then(() => console.log('File is opened'))
          .catch(err => {
            if (err.status === 9) {
              this.errorHandlerService.handle("Keine installierte App kann die Datei mit folgendem Typ öffnen: " + attachment.mimeType);
            } else {
              this.errorHandlerService.handle(err);
            }
          });
        } else {
          // learning resource
    			window.open(attachment.url, "_system", "location=yes");
        }
  		} else {
  			window.open(attachment.url);
  		}
    }
    return Promise.resolve();
  }
}
