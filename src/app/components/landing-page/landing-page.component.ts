import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css'],
})
export class LandingPageComponent {
  templates = [
    { name: 'Business', image: 'assets/templates/business.jpg' },
    { name: 'Creative', image: 'assets/templates/creative.jpg' },
    { name: 'Minimal', image: 'assets/templates/minimal.jpg' },
  ];

  recentSlides = [
    { id: 1, title: 'Marketing Pitch' },
    { id: 2, title: 'Project Proposal' },
  ];

  constructor(private router: Router) {}

  createNewSlide() {
    this.router.navigate(['/ppt-screen']);
  }

  selectTemplate(template: any) {
    this.router.navigate(['/ppt-screen', template.name.toLowerCase()]);
  }

  openSlide(slideId: number) {
    this.router.navigate(['/editor/open', slideId]);
  }
}
