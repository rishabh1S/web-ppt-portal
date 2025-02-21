import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { EditorService } from '../../services/editor.service';
import { QuillModule } from 'ngx-quill';
import Quill from 'quill';
import { ShapeRendererComponent } from '../shape-renderer/shape-renderer.component';

@Component({
  selector: 'app-mainscreen',
  imports: [CommonModule, FormsModule, QuillModule, ShapeRendererComponent],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  selectedSlide: Slide | null = null;
  isEditable = true;

  quillModules = {
    toolbar: false,
    clipboard: {
      matchVisual: false,
    },
  };

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
}
