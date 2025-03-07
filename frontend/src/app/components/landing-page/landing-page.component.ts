import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';
import { lucideCirclePlus, lucideClock, lucideFileUp } from '@ng-icons/lucide';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { switchMap, tap } from 'rxjs';
import { PresentationService } from '../../services/presentation.service';

@Component({
  selector: 'app-landing-page',
  imports: [NgIcon, CommonModule],
  viewProviders: [
    provideIcons({ lucideClock, lucideCirclePlus, lucideFileUp }),
  ],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css'],
})
export class LandingPageComponent {
  templates = [
    { name: 'Business', image: 'https://picsum.photos/id/231/400/225' },
    { name: 'Creative', image: 'https://picsum.photos/id/221/400/225' },
    { name: 'Minimal', image: 'https://picsum.photos/id/233/400/225' },
  ];

  recentSlides = [
    { id: 1, title: 'Q4 Sales Report', lastEdited: '2 days ago' },
    { id: 2, title: 'Product Roadmap', lastEdited: '1 week ago' },
    { id: 3, title: 'Team Building Presentation', lastEdited: '2 weeks ago' },
    { id: 4, title: 'Marketing Strategy', lastEdited: '1 month ago' },
  ];

  constructor(
    private presentationService: PresentationService,
    private router: Router
  ) {}

  createNewSlide() {
    const uniqueId = uuidv4();
    this.router.navigate(['/ppt-screen', uniqueId]);
  }

  selectTemplate(template: any) {
    this.router.navigate(['/ppt-screen', template.name.toLowerCase()]);
  }

  openSlide(slideId: number) {
    this.router.navigate(['/ppt-screen/open', slideId]);
  }

  importPPT(event: any): void {
    const file = event.target.files[0];
    if (!file) return;
  
    this.presentationService.uploadPresentation(file).pipe(
      tap((presentation) => console.log('Uploaded Presentation:', presentation)), // Log the uploaded presentation
      switchMap((presentation) => this.presentationService.getPresentation(presentation.id))
    ).subscribe({
      next: (fullPresentation) => {
        console.log('Full Presentation Data:', fullPresentation);
        this.router.navigate(['/ppt-screen', fullPresentation.id]);
      },
      error: (err) => console.error('Upload failed:', err),
    });
  }
  
}
