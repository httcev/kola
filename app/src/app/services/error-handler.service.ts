import { Injectable }  from '@angular/core';
import { ToastController } from '@ionic/angular';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(
    private toastController: ToastController,
  ) { }

  async handle(error: any): Promise<any> {
    console.log(error);

    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status != null) {
      // server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    } else if (error.code != null) {
      // server-side error
      errorMessage = `Error Code: ${error.code}\nMessage: ${error.message}`;
    } else {
      errorMessage = error;
    }

    const toast = await this.toastController.create({
      message: errorMessage,
      showCloseButton: true,
      closeButtonText: "OK",
      translucent: true,
      position: 'bottom',
      color: 'danger'
    });
    return toast.present();
  }
}
