import { Injectable }  from '@angular/core';
import { CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { FormGroup } from '@angular/forms';
import { AlertController } from '@ionic/angular';
import { Subject, Observable, BehaviorSubject } from 'rxjs';

export interface FormComponent {
  form: FormGroup;
}

@Injectable()
export class CanDeactivateFormComponent implements CanDeactivate<FormComponent> {
  constructor(
    private alertController: AlertController
  ) {}

  canDeactivate(
    component: FormComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean> {
    let result: Subject<boolean> = new Subject<boolean>();
    if (component.form.dirty) {
      result = new Subject<boolean>();
      this.alertController.create({
        header: 'Änderungen verwerfen?',
        buttons: [
          {
            text: 'Abbrechen',
            role: 'cancel',
            cssClass: 'secondary',
            handler: () => {
              result.next(false);
              result.complete();
            }
          }, {
            text: 'Ja',
            handler: () => {
              result.next(true);
              result.complete();
            }
          }
        ]
      }).then(alert => alert.present());
    } else {
      result = new BehaviorSubject<boolean>(true);
    }
    return result;
  }
}
