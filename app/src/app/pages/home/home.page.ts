import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription, BehaviorSubject, timer } from 'rxjs';
import { filter } from 'rxjs/operators';

import { DatabaseService } from '../../services/database.service';
import { SettingsService } from '../../services/settings.service';
import { sortByCreationDate } from '../../util/sort';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage implements OnInit, OnDestroy {
  tasks: Array<any> = null;
  taskCount: number = 0;
  questions: Array<any> = null;
  questionCount: number = 0;
  subscription: Subscription = null;
  showSpinner: BehaviorSubject<boolean> = new BehaviorSubject(false);
  loading: boolean = false;

  constructor(
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

  async load() {
    if (!this.loading) {
      let timerSubscription = timer(500).subscribe(() => { this.showSpinner.next(true) });
      try {
        this.loading = true;

        let docs = await this.databaseService.all('task', false, 'isTemplate="false"');
        this.taskCount = docs.length;
        docs = docs.filter(task => task.done !== true);
        sortByCreationDate(docs);
        this.tasks = docs;

        docs = await this.databaseService.all('question', false);
        this.questionCount = docs.length;
        /*
        let userId = this.settingsService.getCurrentSettings().userId;
        docs = docs.filter(question => question.creator === userId);
        */
        sortByCreationDate(docs, false);
        this.questions = docs.slice(0, 3);
      } catch(err) {
        console.log(err)
        this.tasks = [];
        this.questions = [];
      } finally {
        this.loading = false;
        timerSubscription.unsubscribe();
        this.showSpinner.next(false);
      }
    }
  }

  hideWelcomeText(event) {
    event.target.closest('ion-card').classList.add("removing");
    setTimeout(() => {
      let settings = this.settingsService.getCurrentSettings();
      settings.showWelcomeText = false;
      this.settingsService.save(settings);
    }, 300);
  }
}
