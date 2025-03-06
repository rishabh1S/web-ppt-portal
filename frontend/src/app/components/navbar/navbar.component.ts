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
import { Subscription, take } from 'rxjs';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { PresentationService } from '../../services/presentation.service';
import { ActivatedRoute } from '@angular/router';
import { fontSizes } from '../../../../utils/quill-config';
import { Presentation } from '../../model/Presentation';
import { SlideElement } from '../../model/SlideElements';

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
  private originalPresentation: Presentation | null = null;

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
      this.presentationService
        .getPresentation(this.presentationId)
        .pipe(take(1))
        .subscribe({
          next: (presentation) => {
            this.originalPresentation = JSON.parse(
              JSON.stringify(presentation)
            ); // Deep copy
          },
          error: (error) => {
            console.error('Error retrieving presentation:', error);
          },
        });
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

          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
        },
        error: (err) => {
          console.error('Download failed:', err);
        },
      });
  }

  onSave(): void {
    if (!this.presentationId || !this.originalPresentation) {
      console.error('No presentation ID or original data available');
      return;
    }
    const originalPresentation = this.originalPresentation;

    this.slideService.slides$.pipe(take(1)).subscribe((currentSlides) => {
      const currentPresentation = {
        ...originalPresentation,
        slides: currentSlides,
      };
      let globalChangeDetected = false;

      const updateSlides = currentPresentation.slides.reduce(
        (acc: any[], slide, idx) => {
          if (idx >= originalPresentation.slides.length) {
            globalChangeDetected = true;
            return acc;
          }
          const { slideUpdate, slideChanged } = this.processSlide(
            slide,
            originalPresentation.slides[idx]
          );
          if (slideChanged) globalChangeDetected = true;
          if (slideUpdate) acc.push(slideUpdate);
          return acc;
        },
        []
      );

      const updateData = {
        presentationId: this.presentationId,
        slides: updateSlides,
      };

      if (updateData.slides.length || globalChangeDetected) {
        console.log('Update data:', JSON.stringify(updateData, null, 2));
        this.presentationService
          .updatePresentation(this.presentationId, updateData)
          .subscribe({
            next: () => {
              console.log('Presentation saved successfully');
              // Deep copy to update the reference for future comparisons.
              this.originalPresentation = JSON.parse(
                JSON.stringify(currentPresentation)
              );
            },
            error: (error) =>
              console.error('Error saving presentation:', error),
          });
      } else {
        console.log('No changes detected');
      }
    });
  }

  private processSlide(
    slide: Slide,
    originalSlide: Slide
  ): { slideUpdate: any | null; slideChanged: boolean } {
    const extraElement = slide.elements.length > originalSlide.elements.length;
    const elementsUpdate = slide.elements
      .slice(0, originalSlide.elements.length)
      .map((el: any, idx: number) =>
        this.processElement(el, originalSlide.elements[idx])
      )
      .filter(Boolean);

    const slideChanged = extraElement || elementsUpdate.length > 0;
    return {
      slideUpdate: elementsUpdate.length
        ? { slideId: slide.id, elements: elementsUpdate }
        : null,
      slideChanged,
    };
  }

  private processElement(
    element: SlideElement,
    originalElement: SlideElement
  ): any | null {
    const update: any = { elementId: element.id };

    const compareContent = (): any | null => {
      const { type, content } = element;
      const { content: originalContent } = originalElement;

      switch (type) {
        case 'TEXT':
          if (content.text !== originalContent.text) {
            return {
              ...content,
              text: content.text
                ? this.slideService.convertHtmlToPlainText(content.text)
                : content.text,
            };
          }
          break;
        case 'TABLE':
          if (
            JSON.stringify(content.tableData) !==
            JSON.stringify(originalContent.tableData)
          ) {
            return {
              ...content,
              tableData: content.tableData?.map((row: any[]) =>
                row.map((cell) =>
                  typeof cell === 'string'
                    ? this.slideService.convertHtmlToPlainText(cell)
                    : cell
                )
              ),
            };
          }
          break;
        default:
          if (JSON.stringify(content) !== JSON.stringify(originalContent)) {
            return content;
          }
      }
      return null;
    };

    const contentUpdate = compareContent();
    if (contentUpdate) update.content = contentUpdate;
    if (
      JSON.stringify(element.style) !== JSON.stringify(originalElement.style)
    ) {
      update.style = element.style;
    }
    // Only return an update object if something besides the elementId has been added.
    return Object.keys(update).length > 1 ? update : null;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
