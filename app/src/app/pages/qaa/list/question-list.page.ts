import { Component, OnInit, OnDestroy, NgZone } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import { DatabaseService } from '../../../services/database.service';
import { SettingsService } from '../../../services/settings.service';
import { sortByCreationDate } from '../../../util/sort';

@Component({
  selector: 'app-question-list',
  templateUrl: './question-list.page.html',
  styleUrls: ['./question-list.page.scss']
})
export class QuestionListPage implements OnInit, OnDestroy {
  questions: [] = null;
  subscription: Subscription = null;
  segment: string = "own";

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private ngZone: NgZone,
    private databaseService: DatabaseService,
    private settingsService: SettingsService
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
    // we're using ngZone here because after deleting a question, we're getting here with "location.back()" which obviously is outside angular zone.
    this.ngZone.run(async() => {
      this.questions = null;
      let docs = await this.databaseService.all('question');
      let userId = this.settingsService.getCurrentSettings().userId;
      if (this.segment === 'own') {
        docs = docs.filter(question => question.creator === userId);
      } else {
        docs = docs.filter(question => question.creator !== userId);
      }
      sortByCreationDate(docs, false);
      this.questions = docs;
    });
  }

  create() {
    this.router.navigate(['create'], { relativeTo: this.route })
  }

  segmentChanged(event) {
    this.segment = event.detail.value;
    this.load();
  }
}
