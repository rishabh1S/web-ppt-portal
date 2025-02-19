import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Slide } from '../../model/Slide';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuillModule } from 'ngx-quill';
import { EditorService } from '../../services/editor.service';
import Quill from 'quill';

@Component({
  selector: 'app-slide-view',
  imports: [CommonModule, FormsModule, QuillModule],
  templateUrl: './slide-view.component.html',
  styleUrl: './slide-view.component.css',
})
export class SlideViewComponent {
  @Input() slide!: Slide;
  @Input() isEditable = false;
  @Output() contentUpdate = new EventEmitter<Slide>();

  quillModules = {
    toolbar: false,
    clipboard: {
      matchVisual: false,
    },
  };

  constructor(private editorService: EditorService) {}

  onContentChange(field: string, value: string) {
    const updatedSlide: Slide = {
      ...this.slide,
      content: {
        ...this.slide.content,
        [field]: value,
      },
    };
    this.contentUpdate.emit(updatedSlide);
  }

  onSelectionChanged(event: { editor: Quill; oldRange: any; range: any }) {
    if (event.range) {
      this.editorService.setActiveEditor(event.editor);
    }
  }
}
