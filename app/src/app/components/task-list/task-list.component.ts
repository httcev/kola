import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { SettingsService } from '../../services/settings.service';
import { DatabaseService } from '../../services/database.service';

@Component({
  selector: 'task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent {
  @Input()
  tasks: Array<any> = null;
  currentUserId: number;

  constructor(
    private router: Router,
    private settingsService: SettingsService,
    private databaseService: DatabaseService
  ) {
    this.currentUserId = this.settingsService.getCurrentSettings().userId;
  }

  show(task: any) {
    this.router.navigate(['/task/', task.id])
  }

  trackByFn(index:number, task:any): string {
    return task.id;
  }

  completeTask(index:number, event) {
    event.target.closest('ion-item-sliding').classList.add("removing");
    let task = this.tasks[index];
    task.done = true;
    this.databaseService.save(task);
    setTimeout(() => { this.tasks.splice(index, 1); }, 300);
  }

  removeTask(index:number, event) {
    event.target.closest('ion-item-sliding').classList.add("removing");
    let task = this.tasks[index];
    task.deleted = true;
    this.databaseService.save(task);
    setTimeout(() => { this.tasks.splice(index, 1); }, 300);
  }
}
