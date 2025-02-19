import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { SlideViewComponent } from '../slide-view/slide-view.component';

@Component({
  selector: 'app-mainscreen',
  imports: [CommonModule, FormsModule, SlideViewComponent],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  selectedSlide: Slide | null = null;

  constructor(private slideService: SlideService) {
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
}
