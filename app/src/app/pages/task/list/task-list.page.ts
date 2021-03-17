import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import { DatabaseService } from '../../../services/database.service';
import { sortByCreationDate } from '../../../util/sort';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.page.html',
  styleUrls: ['task-list.page.scss'],
})
export class TaskListPage implements OnInit, OnDestroy {
  tasks: Array<any> = null;
  subscription: Subscription = null;
  segment: string = "new";

  constructor(
    private databaseService: DatabaseService
  ) { }

  ngOnInit() {
    this.subscription = this.databaseService.syncing.pipe(
      filter(val => !val), // only let values equal to "false" pass
    ).subscribe(() => {
        this.load();
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  load() {
    this.tasks = null;
    this.databaseService.all('task', false, 'isTemplate="false"').then(docs => {
      docs = docs.filter(task => task.done === (this.segment !== 'new'));
      sortByCreationDate(docs);
      this.tasks = docs;
    }).catch(() => this.tasks = []);;
  }

  segmentChanged(event) {
    this.segment = event.detail.value;
    this.load();
  }
}
