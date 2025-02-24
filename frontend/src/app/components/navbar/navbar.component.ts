import { Component, Input, OnDestroy } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideSave,
  lucideSettings,
  lucideBold,
  lucideItalic,
  lucideUnderline,
  lucideAlignLeft,
  lucideAlignCenter,
  lucideAlignRight,
  lucideAlignJustify,
  lucideTable,
  lucideImage,
  lucideCircle,
  lucideChevronDown,
  lucideShare2,
  lucideDownload,
} from '@ng-icons/lucide';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Slide } from '../../model/Slide';
import { forkJoin, Subscription } from 'rxjs';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { PresentationService } from '../../services/presentation.service';
import { ActivatedRoute } from '@angular/router';
import PptxGenJS from 'pptxgenjs';
import { fontSizes } from '../../../../utils/quill-config';

@Component({
  selector: 'app-navbar',
  imports: [NgIcon, FormsModule, CommonModule],
  viewProviders: [
    provideIcons({
      lucideAlignJustify,
      lucideSave,
      lucideSettings,
      lucideBold,
      lucideItalic,
      lucideUnderline,
      lucideAlignLeft,
      lucideAlignCenter,
      lucideAlignRight,
      lucideTable,
      lucideImage,
      lucideCircle,
      lucideChevronDown,
      lucideShare2,
      lucideDownload,
    }),
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnDestroy {
  @Input() presentationId!: string;
  title: string = 'My App';
  isDropdownOpen = false;
  isAlignDropdownOpen = false;
  isFontFamilyDropdownOpen = false;
  isFontSizeDropdownOpen = false;
  isHighlightDropdownOpen = false;
  isFontColorDropdownOpen = false;
  selectedSlide: Slide | null = null;
  subscription: Subscription;
  fontSizes: string[] = fontSizes;

  constructor(
    private slideService: SlideService,
    private presentationService: PresentationService,
    private editorService: EditorService,
    private route: ActivatedRoute
  ) {
    this.subscription = this.slideService.selectedSlide$.subscribe(
      (slide) => (this.selectedSlide = slide)
    );
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  toggleAlignDropdown(): void {
    this.isAlignDropdownOpen = !this.isAlignDropdownOpen;
  }

  toggleFontFamilyDropdown(): void {
    this.isFontFamilyDropdownOpen = !this.isFontFamilyDropdownOpen;
  }

  toggleFontSizeDropdown(): void {
    this.isFontSizeDropdownOpen = !this.isFontSizeDropdownOpen;
  }

  toggleHighlightDropdown(): void {
    this.isHighlightDropdownOpen = !this.isHighlightDropdownOpen;
  }

  toggleFontColorDropdown(): void {
    this.isFontColorDropdownOpen = !this.isFontColorDropdownOpen;
  }

  onInsert(type: string): void {
    console.log('Inserting:', type);
    this.isDropdownOpen = false;
  }

  onBoldClick(): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      const currentFormat = editor.getFormat();
      editor.format('bold', !currentFormat['bold']);
    }
  }

  onItalicClick(): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      const currentFormat = editor.getFormat();
      editor.format('italic', !currentFormat['italic']);
    }
  }

  onUnderlineClick(): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      const currentFormat = editor.getFormat();
      editor.format('underline', !currentFormat['underline']);
    }
  }

  onAlign(align: string): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      const currentFormat = editor.getFormat();
      const newAlign = currentFormat['align'] === align ? false : align;
      editor.format('align', newAlign);
    }
    this.isAlignDropdownOpen = false;
  }

  onFontSizeChange(size: string | false): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      editor.format('size', size);
    }
    this.isFontSizeDropdownOpen = false;
  }

  onHighlightChange(color: string | false): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      editor.format('background', color);
    }
    this.isHighlightDropdownOpen = false;
  }

  onFontColorChange(color: string | false): void {
    const editor = this.editorService.getActiveEditor();
    if (editor) {
      editor.format('color', color);
    }
    this.isFontColorDropdownOpen = false;
  }

  onShare(): void {
    const currentUrl = window.location.href;
    navigator.clipboard
      .writeText(currentUrl)
      .then(() => {
        console.log('URL copied to clipboard.');
      })
      .catch((err) => console.error('Error copying URL:', err));
  }

  onSave(): void {
    if (!this.selectedSlide) {
      console.warn('No slide selected to save.');
      return;
    }

    const updateRequests = this.selectedSlide.elements.map((element) =>
      this.presentationService.updateElement(element.id, element.content.text)
    );

    forkJoin(updateRequests).subscribe({
      next: () => {
        console.log('Slide updated successfully');
      },
      error: (err) => console.error('Error updating slide:', err),
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  exportPptx(): void {
    const presentationId = this.route.snapshot.paramMap.get('id');
    if (!presentationId) {
      console.error('No presentation ID found.');
      return;
    }

    this.presentationService.downloadPresentation(presentationId).subscribe({
      next: (data) => this.generatePptx(data),
      error: (err) => console.error('Error fetching presentation:', err),
    });
  }

  generatePptx(data: any): void {
    let ppt = new PptxGenJS();

    // Convert pixels to inches (PowerPoint default is inches)
    const pxToIn = (px: number) => px / 96; // 96px = 1 inch in PPTX

    data.slides.forEach((slide: any) => {
      let pptSlide = ppt.addSlide();

      slide.elements.forEach((element: any) => {
        if (element.type === 'TEXT') {
          pptSlide.addText(element.content, {
            x: pxToIn(element.x),
            y: pxToIn(element.y),
            w: pxToIn(element.width),
            h: pxToIn(element.height),
            fontSize: element.style?.fontSize
              ? element.style.fontSize * 0.75
              : 24,
            color: element.style?.color || '000000',
            bold: element.style?.bold || false,
            italic: element.style?.italic || false,
            align: element.style?.align || 'center',
            valign: element.style?.valign || 'middle',
            wrap: true,
            isTextBox: true,
            margin: [5, 5, 5, 5],
          });
        } else if (element.type === 'IMAGE') {
          pptSlide.addImage({
            path: element.content,
            x: pxToIn(element.x),
            y: pxToIn(element.y),
            w: pxToIn(element.width),
            h: pxToIn(element.height),
          });
        }
      });
    });

    ppt.writeFile({ fileName: 'presentation.pptx' });
  }
}
