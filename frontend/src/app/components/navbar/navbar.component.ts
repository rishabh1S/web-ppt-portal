import { Component, Input, OnDestroy, OnInit } from '@angular/core';
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
import { fontSizes } from '../../../../utils/quill-config';
import PptxGenJS from "pptxgenjs";

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
export class NavbarComponent implements OnInit, OnDestroy {
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

  ngOnInit() {
    this.route.params.subscribe((params) => {
      this.presentationId = params['id'];
    });
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
  
    const updatedSlide = {
      id: this.selectedSlide.id,
      slideNumber: this.selectedSlide.slideNumber,
      elements: this.selectedSlide.elements.map((element) => ({
        id: element.id || this.generateUUID(),
        type: element.type, // Ensure ElementType is included
        content: {
          text: element.content?.text || '',
          url: element.content?.url || '',
          svgPath: element.content?.svgPath || '',
          tableData: element.content?.tableData || [],
          tableHeader: element.content?.tableHeader || []
        },
        x: element.x || 0,
        y: element.y || 0,
        width: element.width || 100, // Default width
        height: element.height || 100, // Default height
        style: {
          ...element.style,
          lineDash: '', // Reset unwanted dashed lines

          backgroundImage: null, // Remove unnecessary SVG backgrounds
        },
      })),
      annotations: this.selectedSlide.annotations || [] // Ensure annotations are included
    };
  
    // Call service to update the slide in the database
    this.presentationService.updateSlide(updatedSlide).subscribe({
      next: () => {
        console.log('Slide saved successfully');
      },
      error: (err) => console.error('Error saving slide:', err),
    });
  }

  generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      const r = (Math.random() * 16) | 0;
      const v = c === 'x' ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    });
  }
  
  
  

  onDownload(): void {
    this.presentationService
      .downloadPresentation(this.presentationId)
      .subscribe({
        next: (data: Blob) => {
          // Create download link
          const url = window.URL.createObjectURL(data);
          const a = document.createElement('a');
          a.href = url;
          a.download = `presentation-${this.presentationId}.pptx`;
          document.body.appendChild(a);
          a.click();

          // Cleanup
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        },
        error: (err) => {
          console.error('Download failed:', err);
          // Add error handling UI feedback here
        },
      });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}