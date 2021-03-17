import { Component } from '@angular/core';
import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import * as moment from 'moment';

import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  public appPages = [
    {
      title: 'Start',
      url: '/home',
      icon: 'home'
    },
    {
      title: 'Lernaufträge',
      url: '/task',
      src: '/assets/icons/task.svg'
    },
    {
      title: 'Fragen',
      url: '/question',
      src: '/assets/icons/question.svg'
    },
    {
      title: 'Einstellungen',
      url: '/settings',
      icon: 'settings'
    }
  ];

  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private notificationService: NotificationService
  ) {
    this.initializeApp();
  }

  initializeApp() {
    moment.locale('de');

    if (this.platform.is('cordova')) {
      this.platform.ready().then(() => {
        this.statusBar.overlaysWebView(false);
        this.statusBar.styleDefault();
        this.statusBar.backgroundColorByHexString('#F5F5F5');
        //this.statusBar.backgroundColorByName("white");
        this.splashScreen.hide();
      });
    }
  }
}
