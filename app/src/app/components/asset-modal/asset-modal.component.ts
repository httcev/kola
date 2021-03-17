import { Component, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';

@Component({
  selector: 'asset-modal',
  templateUrl: './asset-modal.component.html',
  styleUrls: ['./asset-modal.component.scss']
})
export class AssetModalComponent {
  @Input()
  asset:{} = null;

  constructor(
    private modalController: ModalController
  ) { }

  dismissModal() {
    this.modalController.dismiss();
  }
}
