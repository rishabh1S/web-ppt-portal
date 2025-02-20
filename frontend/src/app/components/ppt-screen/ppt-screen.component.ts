import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PresentationService } from '../../services/presentation.service';
import { SlideService } from '../../services/slide.service';
import { Presentation } from '../../model/Presentation';
import { NavbarComponent } from '../navbar/navbar.component';
import { MainscreenComponent } from '../mainscreen/mainscreen.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-ppt-screen',
  imports: [NavbarComponent, MainscreenComponent, SidebarComponent],
  templateUrl: './ppt-screen.component.html',
  styleUrls: ['./ppt-screen.component.css'],
})
export class PptScreenComponent implements OnInit {
  presentationId!: string;
  presentation!: Presentation;

  constructor(
    private route: ActivatedRoute,
    private presentationService: PresentationService,
    private slideService: SlideService
  ) {}

  ngOnInit(): void {
    this.presentationId = this.route.snapshot.paramMap.get('id')!;
    this.loadPresentation(this.presentationId);
  }

  loadPresentation(id: string): void {
    this.presentationService.getPresentation(id).subscribe({
      next: (pres) => {
        // Clear previous slides
        this.slideService.clearSlides();

        // Add new slides
        pres.slides.forEach((slide) => this.slideService.addSlide(slide));
      },
      error: (err) => console.error('Error loading presentation:', err),
    });
  }
}
