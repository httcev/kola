import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { DatabaseService } from '../../../services/database.service';
import { FormComponent } from '../../../util/form';

@Component({
  selector: 'app-answer-edit',
  templateUrl: './answer-edit.page.html'
})
export class AnswerEditPage implements OnInit, FormComponent {
  answer: {} = null;
  form = new FormGroup({
    text: new FormControl()
  });

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private databaseService: DatabaseService
  ) { }

  async ngOnInit() {
    let questionId = this.route.snapshot.paramMap.get('questionId');
    let answerId = this.route.snapshot.paramMap.get('answerId');
    if (answerId) {
      this.answer = await this.databaseService.get(answerId, 'answer');
      this.form.patchValue(this.answer);
    } else {
      this.answer = this.databaseService.createAnswer(questionId);
    }
  }

  async save() {
    this.answer['text'] = this.form.value.text;
    var objectsToSave = [];
    for (let i in this.answer['attachments']) {
      objectsToSave.push(this.answer['attachments'][i]);
    }
		objectsToSave.push(this.answer);
    await this.databaseService.save(objectsToSave);
    this.form.markAsPristine();
    this.router.navigate(['/question/show', this.answer['question']], { replaceUrl: true });
  }
}
