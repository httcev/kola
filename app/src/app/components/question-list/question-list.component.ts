import { Component, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'question-list',
  templateUrl: './question-list.component.html',
  styleUrls: ['./question-list.component.scss']
})
export class QuestionListComponent {
  @Input()
  questions: Array<any> = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  show(question: any) {
    this.router.navigate(['/question/show', question.id])
  }

  trackByFn(index:number, question:any): string {
    return question.id;
  }
}
