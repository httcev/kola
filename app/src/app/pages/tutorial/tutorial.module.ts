import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { RouterModule } from '@angular/router';

import { TutorialPage } from './tutorial.page';

@NgModule({
  imports: [
    CommonModule,
    IonicModule,
    RouterModule.forChild([
      {
        path: '',
        component: TutorialPage
      }
    ])
  ],
  declarations: [TutorialPage]
})
export class TutorialPageModule {}
