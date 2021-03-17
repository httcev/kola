import { Component, Input } from '@angular/core';

@Component({
  selector: 'comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.scss']
})
export class CommentListComponent {
  @Input()
  comments: Array<any> = null;

  constructor(
  ) { }
}
