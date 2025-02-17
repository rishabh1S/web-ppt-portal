import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';

@Component({
  selector: 'app-mainscreen',
  imports: [CommonModule, FormsModule],
  templateUrl: './mainscreen.component.html',
})
export class MainscreenComponent {
  // Hold the currently selected slide
  selectedSlide: Slide | null = null;

  constructor(private slideService: SlideService) {
    this.slideService.selectedSlide$.subscribe((slide) => {
      // Create a new object so two-way binding works properly
      this.selectedSlide = slide ? { ...slide } : null;
    });
  }

  // Call this whenever a bound field is updated
  updateSlide(): void {
    if (this.selectedSlide) {
      this.slideService.updateSlide(this.selectedSlide);  
    }
  }
}
