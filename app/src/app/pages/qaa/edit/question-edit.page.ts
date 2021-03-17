import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { DatabaseService } from '../../../services/database.service';
import { FormComponent } from '../../../util/form';

@Component({
  selector: 'app-question-edit',
  templateUrl: './question-edit.page.html'
})
export class QuestionEditPage implements OnInit, FormComponent {
  question: {} = null;
  form = new FormGroup({
    title: new FormControl('', Validators.required),
    text: new FormControl()
  });

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private databaseService: DatabaseService
  ) { }

  async ngOnInit() {
    let questionId = this.route.snapshot.paramMap.get('questionId');
    if (questionId) {
      this.question = await this.databaseService.get(questionId, 'question');
      this.form.patchValue(this.question);
    } else {
      let reference = null;
      let taskId = this.route.snapshot.paramMap.get('taskId');
      if (taskId != null) {
        let stepIndex = this.route.snapshot.paramMap.get('stepIndex');
        if (stepIndex != null) {
          let task = await this.databaseService.get(taskId, 'task');
          reference = task['steps'][stepIndex]['id'];
        } else {
          reference = taskId;
        }
      }
      this.question = this.databaseService.createQuestion(reference);
    }
  }

  async save() {
    this.question['title'] = this.form.value.title;
    this.question['text'] = this.form.value.text;
    var objectsToSave = [];
    for (let i in this.question['attachments']) {
      objectsToSave.push(this.question['attachments'][i]);
    }
		objectsToSave.push(this.question);
    await this.databaseService.save(objectsToSave);
    this.form.markAsPristine();
    this.router.navigate(['/question/show', this.question['id']], { replaceUrl: true });
  }
}
