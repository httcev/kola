import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

import { DatabaseService } from '../../../services/database.service';
import { FormComponent } from '../../../util/form';

@Component({
  selector: 'app-taskdocumentation-edit',
  templateUrl: './taskdocumentation-edit.page.html'
})
export class TaskDocumentationEditPage implements FormComponent {
  task: {} = null;
  stepIndex: any = null;
  taskDocumentation: {} = null;
  form = new FormGroup({
    text: new FormControl()
  });

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private databaseService: DatabaseService
  ) { }

  async ngOnInit() {
    let taskId = this.route.snapshot.paramMap.get('taskId');
    this.stepIndex = this.route.snapshot.paramMap.get('stepIndex');
    this.task = await this.databaseService.get(taskId, 'task');

    let taskDocumentationId = this.route.snapshot.paramMap.get('taskDocumentationId');
    if (taskDocumentationId) {
      this.taskDocumentation = await this.databaseService.get(taskDocumentationId, 'taskDocumentation');
    } else {
      this.taskDocumentation = this.databaseService.createTaskDocumentation(taskId);
      if (this.stepIndex != null) {
        this.taskDocumentation['reference'] = this.task['steps'][this.stepIndex]['id'];
      }
    }
    this.form.patchValue(this.taskDocumentation);
  }

  async save() {
    this.taskDocumentation['text'] = this.form.value.text;
    var objectsToSave = [];
    for (let i in this.taskDocumentation['attachments']) {
      objectsToSave.push(this.taskDocumentation['attachments'][i]);
    }
		objectsToSave.push(this.taskDocumentation);
    await this.databaseService.save(objectsToSave);
    this.form.markAsPristine();
    let targetRoute = this.stepIndex != null ? ['/task/' + this.task['id'] + '/step/' + this.stepIndex] : ['/task', this.task['id']];
    this.router.navigate(targetRoute, { replaceUrl: true, state: { segment: 'documentations' } });
  }
}
