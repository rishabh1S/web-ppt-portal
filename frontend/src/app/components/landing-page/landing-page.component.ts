import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';
import { lucideCirclePlus, lucideClock, lucideFileUp } from '@ng-icons/lucide';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lastValueFrom } from 'rxjs';
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

  async importPPT(event: any): Promise<void> {
    const file = event.target.files[0];
    if (!file) return;

    try {
      // 1. Upload PPT and get presentation ID
      const presentation = await lastValueFrom(
        this.presentationService.uploadPresentation(file)
      );

      // 2. Fetch the created presentation data
      const fullPresentation = await lastValueFrom(
        this.presentationService.getPresentation(presentation.id)
      );

      console.log('Presentation Data:', fullPresentation);

      // 3. Navigate to presentation editor
      this.router.navigate(['/ppt-screen', presentation.id]);
    } catch (error) {
      console.error('Upload failed:', error);
    }
  }

  extractElementsFromXML(xmlContent: string) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlContent, 'text/xml');
    const elements: any[] = [];

    // Extract text elements
    const texts = xmlDoc.getElementsByTagName('a:t');
    for (let i = 0; i < texts.length; i++) {
      elements.push({
        type: 'text',
        content: texts[i].textContent,
        x: 100,
        y: 50,
        fontSize: 14,
      });
    }

    // Extract image placeholders
    const images = xmlDoc.getElementsByTagName('p:pic');
    for (let i = 0; i < images.length; i++) {
      elements.push({ type: 'image', src: 'placeholder.jpg', x: 200, y: 100 });
    }

    return elements;
  }
}
