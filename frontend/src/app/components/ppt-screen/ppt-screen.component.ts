import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import * as pptx2html from 'pptx2html';
import PptxGenJS from 'pptxgenjs';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { MainscreenComponent } from '../mainscreen/mainscreen.component';

interface SlideElement {
  type: 'text' | 'image';
  content?: string;
  src?: string;
  x: number;
  y: number;
  fontSize?: number;
}

@Component({
  selector: 'app-ppt-screen',
  imports: [
    CommonModule,
    NavbarComponent,
    SidebarComponent,
    MainscreenComponent,
  ],
  templateUrl: './ppt-screen.component.html',
  styleUrl: './ppt-screen.component.css',
})
export class PptScreenComponent implements OnInit {
  slideId: string | null = null;
  slides: SlideElement[][] = [];

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.slideId = params.get('id');

      const storedPpt = sessionStorage.getItem(`ppt-${this.slideId}`);
      if (storedPpt) {
        this.slides = JSON.parse(storedPpt);
      }
    });
  }

  renderSlide(slideXml: string): string {
    return pptx2html.convert(slideXml);
  }

  generatePPT() {
    let pptx = new PptxGenJS();

    this.slides.forEach((slideData) => {
      let slide = pptx.addSlide();

      slideData.forEach((element) => {
        if (element.type === 'text') {
          slide.addText(element.content ?? '', {
            x: element.x / 100,
            y: element.y / 100,
            fontSize: element.fontSize,
          });
        }
      });
    });

    pptx.writeFile({ fileName: `updated-presentation-${this.slideId}.pptx` });
  }

  updateText(slideIndex: number, elementIndex: number, event: any) {
    this.slides[slideIndex][elementIndex].content = event.target.innerText;
    sessionStorage.setItem(`ppt-${this.slideId}`, JSON.stringify(this.slides));
  }
}
