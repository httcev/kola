import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TaskListPage } from './list/task-list.page';
import { TaskDetailPage } from './detail/task-detail.page';
import { TaskDocumentationEditPage } from './detail/taskdocumentation-edit.page';
import { QuestionEditPage } from '../qaa/edit/question-edit.page';
import { FormComponent, CanDeactivateFormComponent } from '../../util/form';

const routes: Routes = [
  {
    path: '',
    component: TaskListPage
  },
  {
    path: ':taskId/editDocumentation/:taskDocumentationId',
    component: TaskDocumentationEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: ':taskId/step/:stepIndex/editDocumentation/:taskDocumentationId',
    component: TaskDocumentationEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: ':taskId/createDocumentation',
    component: TaskDocumentationEditPage,
    canDeactivate: [CanDeactivateFormComponent]
  },
  {
    path: ':taskId/step/:stepIndex/createDocumentation',
    component: TaskDocumentationEditPage,
    canDeactivate: [CanDeactivateFormComponent]

  },
  {
    path: ':taskId/createQuestion',
    component: QuestionEditPage,
    canDeactivate: [CanDeactivateFormComponent]

  },
  {
    path: ':taskId/step/:stepIndex/createQuestion',
    component: QuestionEditPage,
    canDeactivate: [CanDeactivateFormComponent]

  },
  {
    path: ':taskId/step/:stepIndex',
    component: TaskDetailPage
  },
  {
    path: ':taskId',
    component: TaskDetailPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanDeactivateFormComponent]
})
export class TaskRoutingModule {}
