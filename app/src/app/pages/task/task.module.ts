import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { IonicModule } from '@ionic/angular';
import { MomentModule } from 'ngx-moment';

import { ComponentsModule } from '../../components/components.module';
import { TaskRoutingModule } from './task-routing.module';
import { TaskListPage } from './list/task-list.page';
import { TaskDetailPage } from './detail/task-detail.page';
import { TaskDocumentationEditPage } from './detail/taskdocumentation-edit.page';
import { TaskMenuComponent } from './detail/task-menu.component';
import { QaaModule } from '../qaa/qaa.module';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    IonicModule,
    MomentModule,
    MarkdownModule.forRoot(),
    ComponentsModule,
    TaskRoutingModule,
    QaaModule
  ],
  declarations: [
    TaskListPage,
    TaskDetailPage,
    TaskDocumentationEditPage,
    TaskMenuComponent
  ],
  entryComponents: [
    TaskMenuComponent
  ]
})
export class TaskModule {}
