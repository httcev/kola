import { Component, ViewChild, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tutorial',
  templateUrl: 'tutorial.page.html',
  styleUrls: ['tutorial.page.scss'],
})
export class TutorialPage implements OnDestroy {
  @ViewChild('slides', {static:true}) slides: any;
  subscription: Subscription = null;

  slideOpts = {
    initialSlide: 0,
    speed: 400,
  };

  constructor(
    private router: Router
  ) {
    this.subscription = this.router.events.subscribe((e: any) => {
      // If it is a NavigationEnd event re-initalise the component
      if (e instanceof NavigationEnd) {
        this.slideOpts.initialSlide = 0;
      }
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  nextSlide() {
    this.slides.slideNext();
  }
}
