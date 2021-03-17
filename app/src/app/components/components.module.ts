import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MomentModule } from 'ngx-moment';
import { MarkdownModule } from 'ngx-markdown';
import { IonicModule } from '@ionic/angular';
import { SyncControlComponent } from './sync-control/sync-control.component';
import { UserInfoComponent } from './user-info/user-info.component';
import { MediaAttachmentsComponent } from './media-attachments/media-attachments.component';
import { AssetModalComponent } from './asset-modal/asset-modal.component';
import { QuestionListComponent } from './question-list/question-list.component';
import { CommentListComponent } from './comment-list/comment-list.component';
import { TaskReferenceComponent } from './task-reference/task-reference.component';
import { TaskListComponent } from './task-list/task-list.component';

@NgModule({
  declarations: [
    SyncControlComponent,
    UserInfoComponent,
    MediaAttachmentsComponent,
    AssetModalComponent,
    QuestionListComponent,
    CommentListComponent,
    TaskReferenceComponent,
    TaskListComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    IonicModule,
    MomentModule,
    MarkdownModule.forRoot()
  ],
  exports: [
    SyncControlComponent,
    UserInfoComponent,
    MediaAttachmentsComponent,
    AssetModalComponent,
    QuestionListComponent,
    CommentListComponent,
    TaskReferenceComponent,
    TaskListComponent
  ],
  entryComponents: [ ]
})
export class ComponentsModule { }
