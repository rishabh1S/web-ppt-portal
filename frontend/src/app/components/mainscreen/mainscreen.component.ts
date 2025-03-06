import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { QuillModule } from 'ngx-quill';
import Quill from 'quill';
import { quillModules } from '../../../../utils/quill-config';
import { ApplyStylesDirective } from '../../directives/apply-styles.directive';
import { ShapeComponent } from '../shape/shape.component';
import { TableComponent } from '../table/table.component';
import { PresentationService } from '../../services/presentation.service';

@Component({
  selector: 'app-mainscreen',
  imports: [
    CommonModule,
    FormsModule,
    QuillModule,
    ApplyStylesDirective,
    ShapeComponent,
    TableComponent,
  ],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  selectedSlide: Slide | null = null;
  isEditable = true;
  quillModules = quillModules;

  constructor(
    private slideService: SlideService,
    private editorService: EditorService,
    private presentationService: PresentationService
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

  onEditorInitialized(editor: Quill, fontSize: number): void {
    const editorElem = editor.root;
    if (editorElem) {
      // Set font size on the editor's content
      if (fontSize) {
        editorElem.style.fontSize = fontSize + 'px';
      }
      // const containerElem = editorElem.parentElement;
      // if (containerElem) {
      //   containerElem.style.border = 'none';
      // }
    }
  }

  onContentChanged(): void {
    if (this.selectedSlide) {
      this.slideService.updateSlide(this.selectedSlide);
    }
  }

  getImageUrl(url: string): string {
    return this.presentationService.getImageUrl(url);
  }

  onCellContentChanged(event: {
    rowIndex: number;
    cellIndex: number;
    newValue: string;
  }): void {
    if (this.selectedSlide) {
      const element = this.selectedSlide.elements.find(
        (e) => e.type === 'TABLE'
      );
      if (element) {
        element.content.tableData[event.rowIndex][event.cellIndex] =
          event.newValue;
        this.slideService.updateSlide(this.selectedSlide);
      }
    }
  }
}
