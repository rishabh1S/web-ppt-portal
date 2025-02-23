import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { QuillModule } from 'ngx-quill';
import Quill from 'quill';
import { quillModules } from '../../../../utils/quill-config';
import { SlideElement } from '../../model/SlideElements';
import { ApplyStylesDirective } from '../../directives/apply-styles.directive';

@Component({
  selector: 'app-mainscreen',
  imports: [CommonModule, FormsModule, QuillModule, ApplyStylesDirective],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  selectedSlide: Slide | null = null;
  isEditable = true;
  quillModules = quillModules;

  constructor(
    private slideService: SlideService,
    private editorService: EditorService
  ) {
    this.slideService.selectedSlide$.subscribe((slide) => {
      this.selectedSlide = slide ? { ...slide } : null;
    });
  }

  updateSlide(updatedSlide: Slide): void {
    if (updatedSlide) {
      this.selectedSlide = updatedSlide;
      this.slideService.updateSlide(updatedSlide);
    }
  }

  onSelectionChanged(event: { editor: Quill; oldRange: any; range: any }) {
    if (event.range) {
      this.editorService.setActiveEditor(event.editor);
    }
  }

  setEditorFontSize(editor: Quill, fontSize: number): void {
    const editorElem = editor.root;
    if (editorElem && fontSize) {
      editorElem.style.fontSize = fontSize + 'px';
    }
  }

  getTableContent(element: SlideElement): string[][] {
    try {
      return JSON.parse(element.content);
    } catch (error) {
      console.error('Error parsing table content:', error);
      return [];
    }
  }

  /**
   * Retrieves the style for a specific table cell.
   */
  getTableCellStyle(
    element: SlideElement,
    rowIndex: number,
    cellIndex: number
  ): any {
    const cellStyles = element.style?.cellStyles;
    if (cellStyles && cellStyles[rowIndex] && cellStyles[rowIndex][cellIndex]) {
      return cellStyles[rowIndex][cellIndex];
    }
    return {};
  }
}
