import { Component, OnDestroy } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideSave,
  lucideSettings,
  lucideBold,
  lucideItalic,
  lucideUnderline,
  lucideAlignLeft,
  lucideTable,
  lucideImage,
  lucideCircle,
  lucideChevronDown,
  lucideAlignJustify,
  lucideShare2,
} from '@ng-icons/lucide';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Slide } from '../../model/Slide';
import { Subscription } from 'rxjs';
import { SlideService } from '../../services/slide.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgIcon, FormsModule, CommonModule],
  providers: [
    provideIcons({
      hamburger: lucideAlignJustify,
      save: lucideSave,
      settings: lucideSettings,
      bold: lucideBold,
      italic: lucideItalic,
      underline: lucideUnderline,
      alignLeft: lucideAlignLeft,
      table: lucideTable,
      image: lucideImage,
      plusCircle: lucideCircle,
      chevronDown: lucideChevronDown,
      lucideShare: lucideShare2,
    }),
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnDestroy {
  title: string = 'My App';
  isDropdownOpen = false;
  selectedSlide: Slide | null = null;
  subscription: Subscription;

  constructor(private slideService: SlideService) {
    this.subscription = this.slideService.selectedSlide$.subscribe(
      (slide) => (this.selectedSlide = slide)
    );
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onInsert(type: string): void {
    console.log('Inserting:', type);
    this.isDropdownOpen = false;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
