import { Component, Input, OnInit } from '@angular/core';

import { DatabaseService } from '../../services/database.service';

@Component({
  selector: 'task-reference',
  templateUrl: './task-reference.component.html',
  styleUrls: ['./task-reference.component.scss']
})
export class TaskReferenceComponent implements OnInit {
  @Input()
  referrer:{} = null;
  @Input()
  task:{} = null;
  @Input()
  verbose:boolean = false;
  @Input()
  editmode:boolean = false;
  @Input()
  color:string = "light";

  stepIndex:number = -1;

  constructor(
    private databaseService: DatabaseService,
  ) { }

  async ngOnInit() {
    let refId = this.referrer['reference'];
    if (refId && this.task == null) {
      try {
        try {
          this.task = await this.databaseService.get(refId, 'task');
        } catch(e) {
          let step = await this.databaseService.get(refId, 'taskStep');
          this.task = await this.databaseService.get(step['task'], 'task');
        }
      } catch(e) {
        // NOP -> user might not have access to the referred task (via questions)
      }
    }
    if (this.task && this.task['id'] != refId) {
      this.stepIndex = this.task['steps'].map(step => step['id']).indexOf(refId);
    }
  }
}
