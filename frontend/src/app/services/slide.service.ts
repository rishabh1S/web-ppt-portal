import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slide } from '../model/Slide';

@Injectable({
  providedIn: 'root',
})
export class SlideService {
  private slidesSubject = new BehaviorSubject<Slide[]>([]); // BehaviorSubject holds the current value
  slides$ = this.slidesSubject.asObservable(); // Expose as Observable

  private selectedSlideSubject = new BehaviorSubject<Slide | null>(null);
  selectedSlide$ = this.selectedSlideSubject.asObservable();

  // Add a slide
  addSlide(slide: Slide): void {
    const currentSlides = this.slidesSubject.getValue(); // Access current value
    this.slidesSubject.next([...currentSlides, slide]);
    this.selectSlide(slide);
  }

  // Select a slide
  selectSlide(slide: Slide): void {
    this.selectedSlideSubject.next(slide);
  }

  // Update a slide
  updateSlide(updatedSlide: Slide): void {
    const slides = this.slidesSubject.getValue(); // Access current value
    const updatedSlides = slides.map((s) =>
      s.id === updatedSlide.id ? updatedSlide : s
    );
    this.slidesSubject.next(updatedSlides);
    this.selectedSlideSubject.next(updatedSlide);
  }

  // Delete a slide
  deleteSlide(id: string): void {
    const slides = this.slidesSubject.getValue(); // Access current value
    const updatedSlides = slides.filter((s) => s.id !== id);
    this.slidesSubject.next(updatedSlides);
    const currentSelected = this.selectedSlideSubject.getValue();
    if (currentSelected && currentSelected.id === id) {
      this.selectedSlideSubject.next(null);
    }
  }

  // Clear all slides
  clearSlides(): void {
    this.slidesSubject.next([]);
    this.selectedSlideSubject.next(null);
  }
}
