import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { MainscreenComponent } from '../mainscreen/mainscreen.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-ppt-screen',
  imports: [NavbarComponent, MainscreenComponent, SidebarComponent],
  templateUrl: './ppt-screen.component.html',
  styleUrl: './ppt-screen.component.css',
})
export class PptScreenComponent implements OnInit {
  slideId: string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      this.slideId = params.get('id');
    });
  }
}
