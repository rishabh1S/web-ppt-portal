import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { QuillModule } from 'ngx-quill';
import { quillModules } from '../../../../utils/quill-config';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-mainscreen',
  imports: [CommonModule, FormsModule, QuillModule],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  selectedSlide: Slide | null = null;
  isEditable = true;
  quillModules = quillModules;

  constructor(
    private sanitizer: DomSanitizer,
    private slideService: SlideService
  ) {
    this.slideService.selectedSlide$.subscribe((slide) => {
      this.selectedSlide = slide ? { ...slide } : null;
    });
  }

  getSafeHtml(html: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
}
