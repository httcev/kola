import { Injectable }  from '@angular/core';
import { CanActivate, Router } from "@angular/router";
import { BehaviorSubject } from 'rxjs';
import { ToastController } from '@ionic/angular';

export interface Settings {
  url: string,
  user: string,
  password: string,
  userId: number,
  showWelcomeText: boolean
}

@Injectable({
  providedIn: 'root'
})
export class SettingsService implements CanActivate {
  public settings: BehaviorSubject<Settings> = new BehaviorSubject({ url: null, user: null, password: null, userId: -1, showWelcomeText: true });

  constructor(
    private router : Router,
    private toastController: ToastController,
  ) {
    let storedSettings = localStorage["settings"];
    if (storedSettings) {
      this.settings.next(JSON.parse(storedSettings));
    }
  }

  getCurrentSettings(): Settings {
    return this.settings.getValue();
  }

  save(settings: Settings) {
    localStorage["settings"] = JSON.stringify(settings);
    this.settings.next(settings);
  }

  isValid(): boolean {
    let settings = this.getCurrentSettings();
    return (settings && settings.url && settings.user && settings.password) ? true : false;
  }

  async canActivate (): Promise<boolean> {
    if (this.isValid()) {
      return true;
    } else {
      /*
      const toast = await this.toastController.create({ message: 'Willkommen bei KOLA! Bitte geben Sie Ihren Server, Nutzernamen und Passwort ein.', duration: 2000 });
      toast.present();
      */
      this.router.navigateByUrl('/tutorial');
      return false;
    }
  }
}
