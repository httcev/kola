import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, NavigationExtras } from "@angular/router";
import { Platform } from '@ionic/angular';
import { filter, take } from 'rxjs/operators';

import { SettingsService } from './settings.service';
import { NetworkService } from './network.service';
import { DatabaseService } from './database.service';
import { ErrorHandlerService } from './error-handler.service';

declare var PushNotification : any;

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  registrationId: string;

  constructor (
    private settingsService: SettingsService,
    private networkService: NetworkService,
    private databaseService: DatabaseService,
    private errorHandlerService: ErrorHandlerService,
    private httpClient: HttpClient,
    private platform: Platform,
    private router : Router,
    private ngZone: NgZone
  ) {
    this.platform.ready().then(() => {
      if (this.platform.is('cordova')) {
        this.settingsService.settings.subscribe(() => this.transmitRegistrationId());
        this.networkService.online.subscribe((online: boolean) => {
          this.onOnlineStatusChange(online);
        });
      }
    });
  }

  private async onOnlineStatusChange(online: boolean) {
    if (online) {
      let config = await this.requestSenderIdAndChannel();
      if (config && config['senderId']) {
        var options = {
          android: {
            senderID: config['senderId'],
            forceShow: true,
            icon: "notification",
            iconColor: "#a11d21",
            clearNotifications: false,
          },
          ios: {
            alert: "true",
            badge: "true",
            sound: "true",
          },
          windows: {}
        };
        if (config['channel']) {
          options.android["topics"] = [config['channel']];
          options.ios["topics"] = [config['channel']];
        }

        var push = PushNotification.init(options);

        push.on('registration', (data) => {
          this.registrationId = data['registrationId'];
          this.transmitRegistrationId();
        });

        push.on('notification', data => {
          if (!this.networkService.isOnline()) {
            this.errorHandlerService.handle("Das Gerät hat keinen Netzzugang. Bitte schalten Sie eine Netzwerkverbindung ein, damit die Daten geladen werden können.")
          } else {
            if (data.additionalData) {
              let category = data.additionalData.category;
              let referenceId = data.additionalData.referenceId;
              let referenceClass = data.additionalData.referenceClass;
              let route: any[] = null;
              let extras: NavigationExtras = {};
              if (referenceId) {
                if ("new_comments" === category) {
                  if ("TaskDocumentation" === referenceClass) {
                    route = ['/task', referenceId];
                    extras.state = { segment: 'documentations' };
                  }
                  else {
                    route = ['/question/show', referenceId];
                  }
                }
                else if ("new_questions" === category || "new_answers" === category) {
                  route = ['/question/show', referenceId];
                }
                else if ("assigned_tasks" === category) {
                  route = ['/task', referenceId];
                }
                else if ("documented_tasks" === category) {
                  route = ['/task', referenceId];
                  extras.state = { segment: 'documentations' };
                }
              }
              if (route) {
                this.ngZone.run(() => {
                // we need to wait for databaseService's initialization in case the app is not currently active,
                // otherwise sync() will immediately terminate ("no Sync") and we'll get 404's after routing.
                  this.databaseService.initialized.pipe(
                    filter(val => val), // only let values equal to "true" pass
                    take(1) // auto-unsubscribe after first value
                  ).subscribe(async () => {
                    console.log("--- init")
                    try {
                      await this.databaseService.sync();
                      this.databaseService.syncing.pipe(
                        filter(val => !val), // only let values equal to "false" pass
                        take(1) // auto-unsubscribe after first value
                      ).subscribe(() => {
                        console.log("--- synced")
                        this.router.navigate(route, extras);
                        console.log("--- routed to", route)
                        console.log("--- extras", extras);
                        // push.clearNotification();
                      });
                    }
                    catch(e) {
                      console.log(e);
                    }
                  });
                });
              }
            }
          }
        });

        push.on('error', (e) => {
          console.log("on error: ", e);
        });

      }
    }
  }

  private async transmitRegistrationId(): Promise<any> {
    return new Promise((resolve, reject) => {
      if (this.registrationId && this.networkService.isOnline() && this.settingsService.isValid()) {
        let endpoint = this.settingsService.getCurrentSettings().url + '/api/pushToken';
        this.httpClient.post(endpoint, { 'token': this.registrationId }).subscribe(() => {
          resolve();
        }, (error) => {
          this.errorHandlerService.handle(error);
          reject();
        });
      }
      else {
        resolve();
      }
    });
  }

  private requestSenderIdAndChannel(): Promise<any> {
    return new Promise((resolve, reject) => {
      if (this.networkService.isOnline() && this.settingsService.isValid()) {
        let endpoint = this.settingsService.getCurrentSettings().url + '/api/pushToken';
        this.httpClient.get(endpoint).subscribe((response) => {
          resolve(response);
        }, (error) => {
          this.errorHandlerService.handle(error);
          reject();
        });
      }
      else {
        resolve();
      }
    });
  }
}
