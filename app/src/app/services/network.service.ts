import { Injectable, NgZone } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { ToastController } from '@ionic/angular';
import { SettingsService, Settings } from './settings.service';
import { encodeBase64 } from '../util/base64';
import { catchError, finalize } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NetworkService implements HttpInterceptor {
  public online: BehaviorSubject<boolean> = new BehaviorSubject(false);
  public active: BehaviorSubject<boolean> = new BehaviorSubject(false);
  private requestCounter: number = 0;

  constructor(
    private toastController: ToastController,
    private ngZone: NgZone,
    private settingsService: SettingsService
  ) {
    window.addEventListener("online", () => { this.updateOnline(true); });
    window.addEventListener("offline", () => { this.updateOnline(false); });
    this.updateOnline(navigator.onLine);
  }

  public isOnline(): boolean {
    return this.online.getValue();
  }

  public isActive(): boolean {
    return this.active.getValue();
  }

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.updateActive(true);
    let headers = {};
    // don't add content-type header to multipart requests (/api/upload) -> angular will add the header later and manage the request boundaries
    if (!request.headers.has('Content-Type') && request.url.indexOf('/api/upload/') < 0) {
      headers['Content-Type'] = 'application/json;charset=UTF-8';
    }
    if (!request.headers.has('Authorization') && this.settingsService.isValid()) {
      let settings: Settings = this.settingsService.getCurrentSettings();
      headers['Authorization'] = 'Basic ' + encodeBase64(settings.user + ':' + settings.password);
    }

    if (Object.keys(headers).length > 0) {
      request = request.clone({ setHeaders: headers });
    }

    return next.handle(request).pipe(
      /*
      catchError((error: HttpErrorResponse) => {
        let errorMessage = '';
        if (error.error instanceof ErrorEvent) {
          // client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // server-side error
          errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        }
        let toast = this.toastController.create({
          message: errorMessage,
          showCloseButton: true,
          closeButtonText: "OK",
          translucent: true,
          position: 'bottom',
          color: 'danger'
        });
        toast.then(toast => toast.present());

        return throwError(errorMessage);
      }),
      */
      finalize(() => {
        this.updateActive(false);
      })
    );
  }

  private updateOnline(online: boolean) {
    if (online != this.isOnline()) {
      this.ngZone.run(() => { this.online.next(online); })
    }
  }

  private updateActive(increment: boolean) {
    this.requestCounter = this.requestCounter + (increment ? 1 : - 1);
    console.log("--- pending requests", this.requestCounter);
    let active = this.requestCounter > 0;
    if (active != this.isActive()) {
      this.ngZone.run(() => {
        this.active.next(active);
      });
    }
  }
}
