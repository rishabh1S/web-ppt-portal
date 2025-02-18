import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';
import JSZip from 'jszip';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing-page',
  imports: [CommonModule],
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
    const uniqueId = uuidv4(); 
    this.router.navigate(['/ppt-screen', uniqueId]);
  }

  selectTemplate(template: any) {
    this.router.navigate(['/ppt-screen', template.name.toLowerCase()]);
  }

  openSlide(slideId: number) {
    this.router.navigate(['/ppt-screen/open', slideId]);
  }

  async importPPT(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    const uniqueId = uuidv4();
    const reader = new FileReader();

    reader.onload = async (e) => {
      const zip = await JSZip.loadAsync(e.target?.result as ArrayBuffer);
      const slideData: any[] = [];

      // Extract slides
      const slideFiles = Object.keys(zip.files).filter(filename => filename.startsWith('ppt/slides/slide'));
      for (const filename of slideFiles) {
        const xmlContent = await zip.files[filename].async("text");
        slideData.push(this.extractElementsFromXML(xmlContent));
      }

      sessionStorage.setItem(`ppt-${uniqueId}`, JSON.stringify(slideData));
      this.router.navigate(['/ppt-screen', uniqueId]);
    };

    reader.readAsArrayBuffer(file);
  }

  extractElementsFromXML(xmlContent: string) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlContent, "text/xml");
    const elements: any[] = [];

    // Extract text elements
    const texts = xmlDoc.getElementsByTagName("a:t");
    for (let i = 0; i < texts.length; i++) {
      elements.push({ type: "text", content: texts[i].textContent, x: 100, y: 50, fontSize: 14 });
    }

    // Extract image placeholders
    const images = xmlDoc.getElementsByTagName("p:pic");
    for (let i = 0; i < images.length; i++) {
      elements.push({ type: "image", src: "placeholder.jpg", x: 200, y: 100 });
    }

    return elements;
  }
}
