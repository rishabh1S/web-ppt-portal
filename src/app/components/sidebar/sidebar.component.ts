import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideCirclePlus, lucideTrash } from '@ng-icons/lucide';
import { Observable } from 'rxjs';
import { Slide } from '../../model/Slide';
import { SlideService } from '../../services/slide.service';
import { SlideViewComponent } from '../slide-view/slide-view.component';

@Component({
  selector: 'app-sidebar',
  imports: [NgIcon, CommonModule, SlideViewComponent],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  viewProviders: [provideIcons({ lucideCirclePlus, lucideTrash })],
})
export class SidebarComponent {
  slides: Observable<Slide[]>;
  selectedSlide$: Observable<Slide | null>;

  constructor(private slideService: SlideService) {
    this.slides = this.slideService.slides$;
    this.selectedSlide$ = this.slideService.selectedSlide$;
  }

  addSlide(): void {
    const newSlide: Slide = {
      id: Date.now(),
      template: 'title',
      content: {
        title: 'Enter Title',
        subtitle: 'Enter Subtitle',
      },
    };
    this.slideService.addSlide(newSlide);
  }

  // Delete a slide
  deleteSlide(id: number): void {
    this.slideService.deleteSlide(id);
  }

  // Select a slide so that its details show up in the main screen
  selectSlide(slide: Slide): void {
    this.slideService.selectSlide(slide);
  }
}
