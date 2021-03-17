import { Component, Input, OnInit } from '@angular/core';

import { DatabaseService } from '../../services/database.service';

@Component({
  selector: 'user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss']
})
export class UserInfoComponent implements OnInit {
  @Input()
  ref: any = {};
  @Input()
  refProperty: string = 'creator';
  @Input()
  dateProperty: string = 'dateCreated';
  @Input()
  color: string = 'medium';

  displayName: string = null;
  date: string = null;
  prefixIcon: string = null;

  constructor(
    private databaseService: DatabaseService
  ) { }

  async ngOnInit() {
    let user = this.ref[this.refProperty];
    if (!isNaN(user)) {
      user = await this.databaseService.get(user, 'user', false, true);
    }
    this.displayName = user ? user.displayName : 'n/a';
    this.date = this.ref[this.dateProperty];
    switch (this.ref['_table']) {
      case 'taskDocumentation': this.prefixIcon = '/assets/icons/compose.svg';
        break;
      case 'question': this.prefixIcon = '/assets/icons/question.svg';
        break;
      case 'answer': this.prefixIcon = '/assets/icons/answer.svg';
        break;
      case 'task': this.prefixIcon = '/assets/icons/task.svg';
        break;
      case 'comment': this.prefixIcon = '/assets/icons/comment.svg';
        break;
    }
  }
}
