import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { QuestionListPage } from './list/question-list.page';
import { QuestionDetailPage } from './detail/question-detail.page';
import { QuestionEditPage } from './edit/question-edit.page';
import { AnswerEditPage } from './answer/answer-edit.page';
import { FormComponent, CanDeactivateFormComponent } from '../../util/form';

const routes: Routes = [
  {
    path: '',
    component: QuestionListPage
  },
  {
    path: 'edit/:questionId',
    component: QuestionEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: 'create',
    component: QuestionEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: 'show/:questionId',
    component: QuestionDetailPage
  },
  {
    path: 'show/:questionId/editAnswer/:answerId',
    component: AnswerEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: 'show/:questionId/createAnswer',
    component: AnswerEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanDeactivateFormComponent]
})
export class QaaRoutingModule {}
