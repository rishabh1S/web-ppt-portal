import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../components/navbar/navbar.component';
import { MainscreenComponent } from '../components/mainscreen/mainscreen.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-ppt-screen',
  imports: [NavbarComponent,
      MainscreenComponent,
      SidebarComponent],
  templateUrl: './ppt-screen.component.html',
  styleUrl: './ppt-screen.component.css'
})
export class PptScreenComponent implements OnInit {
  slideId: string | null = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.slideId = params.get('id');
    });
  }
}
