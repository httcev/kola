import { Component, Input } from '@angular/core';

import { DatabaseService } from '../../../services/database.service';

@Component({
  selector: 'task-menu',
  templateUrl: './task-menu.component.html'
})
export class TaskMenuComponent {
	@Input() task: any;

	constructor(
		private databaseService: DatabaseService
	) { }

	toggleDone() {
    if (this.task) {
  		setTimeout(() => {
  			this.task['done'] = !this.task['done'];
  			this.databaseService.save(this.task);
  		});
    }
	}
}
