import { Component } from '@angular/core';
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
} from '@ng-icons/lucide';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgIcon, FormsModule, CommonModule],
  providers: [
    provideIcons({
      lucideAlignJustify: lucideAlignJustify,
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
    }),
  ],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  title: string = 'My App';
  isDropdownOpen = false;

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onInsert(type: string): void {
    console.log('Inserting:', type);
    this.isDropdownOpen = false;
  }
}
