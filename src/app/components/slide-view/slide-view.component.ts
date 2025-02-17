import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Slide } from '../../model/Slide';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-slide-view',
  imports: [CommonModule, FormsModule],
  templateUrl: './slide-view.component.html',
  styleUrl: './slide-view.component.css',
})
export class SlideViewComponent {
  @Input() slide!: Slide;
  @Input() isEditable = false;
  @Output() contentUpdate = new EventEmitter<Slide>();

  onContentChange(field: string, value: string) {
    const updatedSlide = {
      ...this.slide,
      content: {
        ...this.slide.content,
        [field]: value,
      },
    };
    this.contentUpdate.emit(updatedSlide);
  }
}
