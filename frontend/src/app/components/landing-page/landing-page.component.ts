import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';
import { lucideCirclePlus, lucideClock, lucideFileUp } from '@ng-icons/lucide';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { PptService } from '../../services/ppt.service';

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
    { name: 'Business', image: 'assets/templates/business.jpg' },
    { name: 'Creative', image: 'assets/templates/creative.jpg' },
    { name: 'Minimal', image: 'assets/templates/minimal.jpg' },
  ];

  recentSlides = [
    { id: 1, title: 'Q4 Sales Report', lastEdited: '2 days ago' },
    { id: 2, title: 'Product Roadmap', lastEdited: '1 week ago' },
    { id: 3, title: 'Team Building Presentation', lastEdited: '2 weeks ago' },
    { id: 4, title: 'Marketing Strategy', lastEdited: '1 month ago' },
  ];

  constructor(private pptService: PptService, private router: Router) {}

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
      const response = await this.pptService.uploadPpt(file);
      console.log('Upload success:', response);
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
