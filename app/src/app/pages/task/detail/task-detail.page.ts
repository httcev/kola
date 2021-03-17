import { Component, OnInit, NgZone } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { PopoverController, ActionSheetController, AlertController } from '@ionic/angular';

import { TaskMenuComponent } from './task-menu.component';
import { DatabaseService } from '../../../services/database.service';
import { sortByCreationDate } from '../../../util/sort';
import { sortByLastUpdatedDate } from '../../../util/sort';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.page.html',
  styleUrls: ['task-detail.page.scss']
})
export class TaskDetailPage implements OnInit {
  segment: string = "task";
  loading: boolean = true;
  task: {} = null;
  currentStep: {} = null;
  currentStepIndex: number = -1;
  documentations: Array<any> = null;
  questions: Array<any> = null;
  reflectionAnswers: {} = null;

  constructor(
    private ngZone: NgZone,
    private router: Router,
    private route: ActivatedRoute,
    private popoverController: PopoverController,
    private actionSheetController: ActionSheetController,
    private alertController: AlertController,
    private databaseService: DatabaseService
  ) { }

  async ngOnInit() {
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.loading = true;
        let state = this.router.getCurrentNavigation().extras.state;
        if (state && state.segment) {
          this.segment = state.segment;
        }
        return this.databaseService.get(params.get('taskId'), 'task')
      })
    ).subscribe(task => {
      this.ngZone.run(async() => {
        try {
          this.task = task;
          let stepParam = this.route.snapshot.paramMap.get('stepIndex');
          if (!isNaN(+stepParam)) {
            let stepIndex = parseInt(stepParam);
            if (stepIndex >= 0 && task.steps && task.steps.length > stepIndex) {
              this.currentStepIndex = stepIndex;
              this.currentStep = task.steps[stepIndex];
            }
          }

          // load task documentations
          let taskRefs = this.getPossibleTaskReferences();
          this.documentations = await this.databaseService.all('taskDocumentation', true, 'reference in ' + taskRefs);
          sortByLastUpdatedDate(this.documentations, false);

          // load task related questions
          this.questions = await this.databaseService.all('question', true, 'reference in ' + taskRefs);
          sortByCreationDate(this.questions, false);

          // load reflection answers
          let taskReflectionAnswers = await this.databaseService.all('reflectionAnswer', true, 'task="' + this.task['id'] + '"');
          this.reflectionAnswers = {};
          for (let reflectionAnswer of taskReflectionAnswers) {
            this.reflectionAnswers[reflectionAnswer['question']] = reflectionAnswer;
          }
          // create empty reflection answers when not existing
          for (let reflectionQuestion of this.task['reflectionQuestions']) {
            if (!this.reflectionAnswers[reflectionQuestion['id']]) {
              this.reflectionAnswers[reflectionQuestion['id']] = this.databaseService.createReflectionAnswer(this.task['id'], reflectionQuestion['id']);
            }
          }
        } finally {
          this.loading = false;
        }
      });
    }, err => {
      this.loading = false;
    });
  }

  async openMenu(ev: any) {
    const popover = await this.popoverController.create({
      component: TaskMenuComponent,
      event: ev,
      showBackdrop: false,
      componentProps: { task: this.task}
    });
    return await popover.present();
  }

  async openActionSheet(documentation) {
    let buttons = [];
    if (this.databaseService.canEdit(documentation)) {
      buttons.push({
        text: 'Löschen',
        role: 'destructive',
        icon: 'trash',
        handler: async() => {
          this.openAlertConfirmDelete(documentation);
        }
      });
      buttons.push({
        text: 'Bearbeiten',
        icon: 'create',
        handler: () => {
          this.router.navigate(['editDocumentation', documentation['id']], {relativeTo: this.route});
        }
      });
    }
    buttons.push({
      text: 'Kommentieren',
      icon: 'chatbubbles',
      handler: () => {
        this.openCommentPrompt(documentation);
      }
    });
    buttons.push({
      text: 'Abbrechen',
      icon: 'close',
      role: 'cancel'
    });

    const actionSheet = await this.actionSheetController.create({ header: 'Dokumentation', buttons: buttons });
    await actionSheet.present();
  }

  async openCommentPrompt(documentation) {
    const alert = await this.alertController.create({
      header: 'Ihr Kommentar:',
      inputs: [
        {
          name: 'comment',
//          type: 'textarea',
          type: 'text',
          placeholder: 'Geben Sie hier Ihr Kommentar ein...'
        }
      ],
      buttons: [
        {
          text: 'Abbrechen',
          role: 'cancel',
          cssClass: 'secondary'
        }, {
          text: 'Abschicken',
          handler: async(data) => {
            if (data.comment.length) {
              let comment = this.databaseService.createComment(documentation['id']);
              comment['text'] = data.comment;
              documentation['_comments'].push(comment);
              await this.databaseService.save(comment);
            }
          }
        }
      ]
    });

    await alert.present();
  }

  async openAlertConfirmDelete(documentation) {
    const alert = await this.alertController.create({
      header: 'Dokumentation löschen?',
      //message: 'Message <strong>text</strong>!!!',
      buttons: [
        {
          text: 'Abbrechen',
          role: 'cancel',
          cssClass: 'secondary'
        }, {
          text: 'Löschen',
          handler: async() => {
            documentation['deleted'] = true;
            await this.databaseService.save(documentation);
            const index: number = this.documentations.indexOf(documentation);
            if (index !== -1) {
              this.documentations.splice(index, 1);
            }
          }
        }
      ]
    });

    await alert.present();
  }

  async openReflectionAnswerPrompt(reflectionAnswer, rating: string) {
    const alert = await this.alertController.create({
      header: 'Ihre Anmerkungen:',
      inputs: [
        {
          name: 'text',
//          type: 'textarea',
          type: 'text',
          placeholder: 'Geben Sie hier optional Ihre Anmerkungen ein...',
          value: reflectionAnswer.text
        }
      ],
      buttons: [
        {
          text: 'OK',
          handler: async(data) => {
            reflectionAnswer.rating = rating;
            reflectionAnswer.text = data.text;
            await this.databaseService.save(reflectionAnswer);
          }
        }
      ]
    });

    await alert.present();
  }

  segmentChanged(event) {
    this.segment = event.detail.value;
  }

  currentStepChanged(stepIndex: number) {
    this.router.navigate(['step', stepIndex], {relativeTo: this.route})
  }

  createTaskDocumentation() {
    this.router.navigate(['createDocumentation'], {relativeTo: this.route})
  }

  createQuestion() {
    this.router.navigate(['createQuestion'], {relativeTo: this.route})
  }

  async setReflectionAnswer(reflectionAnswer, rating: string) {
    this.openReflectionAnswerPrompt(reflectionAnswer, rating);
  }

  private getPossibleTaskReferences(): string {
		let referenceIds = "('" + this.task['id'] + "'";
    for (let step of this.task['steps']) {
      let stepId = typeof step === 'object' ? step['id'] : step;
      referenceIds += ",'" + stepId + "'";
    }
		referenceIds += ")";
		return referenceIds;
	}
}
