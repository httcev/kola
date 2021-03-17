import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Location } from '@angular/common';
import { switchMap } from 'rxjs/operators';
import { PopoverController, ActionSheetController, AlertController } from '@ionic/angular';

import { DatabaseService } from '../../../services/database.service';

@Component({
  selector: 'app-question-detail',
  templateUrl: './question-detail.page.html'
})
export class QuestionDetailPage implements OnInit {
  loading: boolean = true;
  question: {} = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    private popoverController: PopoverController,
    private actionSheetController: ActionSheetController,
    private alertController: AlertController,
    private databaseService: DatabaseService
  ) { }

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.loading = true;
        return this.databaseService.get(params.get('questionId'), 'question')
      })
    ).subscribe(question => {
      this.question = question;
      this.loading = false;
    }, err => {
      this.loading = false;
    });
  }

  async openActionSheet(ref) {
    let isQuestion = ref['_table'] === 'question';
    let buttons = [];
    if (this.databaseService.canEdit(ref)) {
      buttons.push({
        text: 'Löschen',
        role: 'destructive',
        icon: 'trash',
        handler: async() => {
          this.openAlertConfirmDelete(ref);
        }
      });
      buttons.push({
        text: 'Bearbeiten',
        icon: 'create',
        handler: () => {
          if (isQuestion) {
            this.router.navigate(['/question/edit', ref['id']])
          } else {
            this.router.navigate(['editAnswer', ref['id']], { relativeTo: this.route })
          }
        }
      });
    }
    buttons.push({
      text: 'Kommentieren',
      icon: 'chatbubbles',
      handler: () => {
        this.openCommentPrompt(ref);
      }
    });
    if (!isQuestion && this.databaseService.canEdit(this.question)) {
      if (this.question['acceptedAnswer'] !== ref['id']) {
        buttons.push({
          text: 'Als richtige Antwort markieren',
          icon: 'checkmark',
          handler: async() => {
            this.question['acceptedAnswer'] = ref['id'];
            await this.databaseService.save(this.question);
          }
        });
      }
      else {
        buttons.push({
          text: 'Markierung als richtige Antwort entfernen',
          icon: 'checkmark',
          handler: async() => {
            this.question['acceptedAnswer'] = null;
            await this.databaseService.save(this.question);
          }
        });
      }
    }
    buttons.push({
      text: 'Abbrechen',
      icon: 'close',
      role: 'cancel'
    });

    let header = (ref['_table'] === 'question') ? 'Frage' : 'Antwort';
    const actionSheet = await this.actionSheetController.create({ header: header, buttons: buttons });
    await actionSheet.present();
  }

  async openCommentPrompt(ref) {
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
              let comment = this.databaseService.createComment(ref['id']);
              comment['text'] = data.comment;
              ref['_comments'].push(comment);
              await this.databaseService.save(comment);
            }
          }
        }
      ]
    });

    await alert.present();
  }

  async openAlertConfirmDelete(ref) {
    let header = (ref['_table'] === 'question') ? 'Frage' : 'Antwort';
    header += ' löschen?';
    const alert = await this.alertController.create({
      header: header,
      //message: 'Message <strong>text</strong>!!!',
      buttons: [
        {
          text: 'Abbrechen',
          role: 'cancel',
          cssClass: 'secondary'
        }, {
          text: 'Löschen',
          handler: async() => {
            ref['deleted'] = true;
            await this.databaseService.save(ref);
            if (ref['_table'] === 'question') {
              this.location.back();
            } else {
              const index: number = this.question['_answers'].indexOf(ref);
              if (index !== -1) {
                this.question['_answers'].splice(index, 1);
              }
            }
          }
        }
      ]
    });

    await alert.present();
  }

  createAnswer() {
    this.router.navigate(['createAnswer'], { relativeTo: this.route })
  }
}
