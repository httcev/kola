import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MomentModule } from 'ngx-moment';
import { MarkdownModule } from 'ngx-markdown';

import { IonicModule } from '@ionic/angular';

import { ComponentsModule } from '../../components/components.module';
import { QaaRoutingModule } from './qaa-routing.module';
import { QuestionListPage } from './list/question-list.page';
import { QuestionDetailPage } from './detail/question-detail.page';
import { QuestionEditPage } from './edit/question-edit.page';
import { AnswerEditPage } from './answer/answer-edit.page';

@NgModule({
  imports: [
    CommonModule,
    ComponentsModule,
    ReactiveFormsModule,
    IonicModule,
    MomentModule,
    MarkdownModule.forRoot(),
    ComponentsModule,
    QaaRoutingModule
  ],
  declarations: [ QuestionListPage, QuestionDetailPage, QuestionEditPage, AnswerEditPage ],
  entryComponents: [ ],
  exports: [ QuestionEditPage, AnswerEditPage ]
})
export class QaaModule {}
