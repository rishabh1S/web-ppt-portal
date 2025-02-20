import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slide } from '../model/Slide';

@Injectable({
  providedIn: 'root',
})
export class SlideService {
  private slidesSubject = new BehaviorSubject<Slide[]>([]);
  slides$ = this.slidesSubject.asObservable();

  private selectedSlideSubject = new BehaviorSubject<Slide | null>(null);
  selectedSlide$ = this.selectedSlideSubject.asObservable();

  addSlide(slide: Slide): void {
    const currentSlides = this.slidesSubject.getValue();
    this.slidesSubject.next([...currentSlides, slide]);
    this.selectSlide(slide);
  }

  selectSlide(slide: Slide): void {
    this.selectedSlideSubject.next(slide);
  }

  updateSlide(updatedSlide: Slide): void {
    const slides = this.slidesSubject
      .getValue()
      .map((s) => (s.id === updatedSlide.id ? updatedSlide : s));
    this.slidesSubject.next(slides);
    this.selectedSlideSubject.next(updatedSlide);
  }

  deleteSlide(id: string): void {
    const slides = this.slidesSubject.getValue().filter((s) => s.id !== id);
    this.slidesSubject.next(slides);
    const currentSelected = this.selectedSlideSubject.getValue();
    if (currentSelected && currentSelected.id === id) {
      this.selectedSlideSubject.next(null);
    }
  }

  clearSlides(): void {
    this.slidesSubject.next([]);
    this.selectedSlideSubject.next(null);
  }
}
