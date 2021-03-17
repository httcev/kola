import { Component, Input } from '@angular/core';
import { FileChooser } from '@ionic-native/file-chooser/ngx';
import { Camera, CameraOptions } from '@ionic-native/camera/ngx';
import { MediaCapture, MediaFile, CaptureError } from '@ionic-native/media-capture/ngx';
import { Platform } from '@ionic/angular';

import { ErrorHandlerService } from '../../services/error-handler.service';
import { DatabaseService } from '../../services/database.service';
import { AssetsService } from '../../services/assets.service';

interface Window {
  resolveLocalFileSystemURL: any;
}
declare var window : Window;

@Component({
  selector: 'media-attachments',
  templateUrl: './media-attachments.component.html',
  styleUrls: ['./media-attachments.component.scss']
})
export class MediaAttachmentsComponent {
  @Input()
  ref:{} = null;
  @Input()
  refProp:string = 'attachments';
  @Input()
  header:string = 'Anhänge';
  @Input()
  headerIcon:string = 'attach';
  @Input()
  editmode:boolean = false;

  constructor(
    private platform: Platform,
    private fileChooser: FileChooser,
    private camera: Camera,
    private mediaCapture: MediaCapture,
    private errorHandlerService: ErrorHandlerService,
    private databaseService: DatabaseService,
    private assetsService: AssetsService
  ) { }

  async attachVideo(): Promise<any> {
    let options = { limit: 1, quality: 0 }
    return this.mediaCapture.captureVideo(options).then((data: MediaFile[]) => {
      let mediaFile = data[0];
      return this.attachFile(mediaFile.fullPath);
    }).catch(err => {
      // status 3 means user canceled capture
      if (err.code !== 3) {
        this.errorHandlerService.handle(err);
        return Promise.reject("capture video failed");
      }
      return Promise.resolve();
    });;
  }

  async attachPhoto(): Promise<any> {
    const options: CameraOptions = {
      quality: 100,
      sourceType: this.camera.PictureSourceType.CAMERA,
      destinationType: this.platform.is('android') ? this.camera.DestinationType.FILE_URI : this.camera.DestinationType.NATIVE_URI,
      encodingType: this.camera.EncodingType.JPEG,
      mediaType: this.camera.MediaType.PICTURE,
      saveToPhotoAlbum: false,
      correctOrientation: true,
      targetWidth: 1280,
      targetHeight: 1280
    }
    return this.camera.getPicture(options).then((imageUrl) => {
      return this.attachFile(imageUrl);
    }).catch(err => {
      // only display error if not canceled by user
      if (typeof err !== "string" || (err.indexOf("cancel") < 0 && err.indexOf("No Image Selected") < 0)) {
        this.errorHandlerService.handle(err);
      }
      return Promise.reject();
    });
  }

  async attachSelected(): Promise<{}> {
    return this.fileChooser.open().then(uri => {
      return this.attachFile(uri);
    }).catch(err => {
      if ('User canceled.' === err) {
        Promise.resolve();
      } else {
        this.errorHandlerService.handle(err)
        return Promise.reject("choosing file failed");
      }
    });
  }

  removeAttachment(index, event) {
    event.target.closest('ion-item-sliding').classList.add("removing");
    setTimeout(() => { this.ref[this.refProp].splice(index, 1); }, 300);
  }

  private async attachFile(uri: string): Promise<{}> {
    return new Promise((resolve, reject) => {
      window.resolveLocalFileSystemURL(uri, fileEntry => {
				fileEntry.file(file => {
          let attachment = this.databaseService.createAttachment(this.ref);
          attachment['name'] = fileEntry.name;
					attachment['mimeType'] = file.type;

          this.assetsService.copyToAssetsFolder(uri, attachment).then(() => {
            resolve(attachment)
          }, (err) => {
            this.errorHandlerService.handle(err);
            reject(err);
          });
				});
			}, (err) => {
        this.errorHandlerService.handle(err);
        reject(err);
      });
    });
  }
}
