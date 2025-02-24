import { Component, OnDestroy } from '@angular/core';
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
} from '@ng-icons/lucide';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Slide } from '../../model/Slide';
import { forkJoin, Subscription } from 'rxjs';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { PresentationService } from '../../services/presentation.service';
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
    }),
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnDestroy {
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
    private editorService: EditorService
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
      this.presentationService.updateElement(element.id, element.content)
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
}
