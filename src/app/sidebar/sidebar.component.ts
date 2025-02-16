import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucidePlus, lucideTrash } from '@ng-icons/lucide';

@Component({
  selector: 'app-sidebar',
  imports: [NgIcon, CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  viewProviders: [provideIcons({ lucidePlus, lucideTrash })],
})
export class SidebarComponent {
  slides: number[] = [1];

  addSlide() {
    this.slides.push(this.slides.length + 1);
  }

  deleteSlide(index: number) {
    this.slides.splice(index, 1);
  }
}
