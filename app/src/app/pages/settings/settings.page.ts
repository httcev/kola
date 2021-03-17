import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { ToastController, Platform } from '@ionic/angular';
import { AppVersion } from '@ionic-native/app-version/ngx';

import { SettingsService, Settings } from '../../services/settings.service';
import { NetworkService } from '../../services/network.service';
import { encodeBase64 } from '../../util/base64';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss']
})
export class SettingsPage implements OnInit, OnDestroy {
  private subscription: Subscription;
  private version: string = null;

  settingsForm = new FormGroup({
    url: new FormControl('', Validators.required),
    user: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    showWelcomeText: new FormControl(),
  });

  constructor(
    private settingsService: SettingsService,
    private networkService: NetworkService,
    private router: Router,
    private toastController: ToastController,
    private httpClient: HttpClient,
    private platform: Platform,
    private appVersion: AppVersion
  ) { }

  async ngOnInit() {
    this.subscription = this.settingsService.settings.subscribe((settings: Settings) => {
      if (settings) {
        this.settingsForm.patchValue(settings);
      }
    });
    this.version = this.platform.is('cordova') ? await this.appVersion.getVersionNumber() : 'dummy';
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  async save() {
    var url = this.settingsForm.value.url;
    if (url.indexOf("http://") !== 0 && url.indexOf("https://") !== 0) {
      this.settingsForm.patchValue({ url: "https://" + url});
    }

    if (!this.networkService.isOnline()) {
      const toast = await this.toastController.create({
        message: 'Das Gerät hat keinen Netzzugang. Bitte schalten Sie eine Netzwerkverbindung ein, damit die Einstellungen geprüft werden können.',
        duration: 4000,
        color: 'danger'
      });
      toast.present();
      return;
    }

    let result = await this.verifiy();
    if (result['status'] === 200) {
      let settings = this.settingsForm.value;
      settings['userId'] = result['userId'];
      this.settingsService.save(settings);

      const toast = await this.toastController.create({
        message: 'Einstellungen wurden gespeichert.',
        duration: 2000,
        color: 'success'
      });
      toast.present();
      this.router.navigate(['/']);
    }
    else {
      let message: string;
      if (result['status'] === 401) {
        message = 'Benutzer und/oder Passwort sind nicht korrekt.';
      }
      else {
        message = 'Server-Adresse ist nicht korrekt';
      }
      const toast = await this.toastController.create({
        message: message,
        duration: 2000,
        color: 'danger'
      });
      toast.present();
    }
  }

  async verifiy() : Promise<{}> {
    return new Promise((resolve) => {
      let result = { 'status':0, 'userId':null };
      let url = this.settingsForm.value.url + '/api/check';
      let token = 'Basic ' + encodeBase64(this.settingsForm.value.user + ':' + this.settingsForm.value.password);
      this.httpClient.get(url, { headers: new HttpHeaders().set('Authorization', token)}).subscribe((response) => {
        result.status = 200;
        result.userId = response['userId'];
        resolve(result);
      }, (error) => {
        result.status = error.status;
        resolve(result);
      });
    });
  }

  logout() {
    this.settingsForm.patchValue({user:'', password:''});
    let settings = this.settingsForm.value;
    settings['userId'] = null;
    this.settingsService.save(settings);
  }
}
