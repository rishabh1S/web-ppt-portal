import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MainscreenComponent } from './components/mainscreen/mainscreen.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { PptScreenComponent } from './ppt-screen/ppt-screen.component';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    NavbarComponent,
    MainscreenComponent,
    SidebarComponent,
    LandingPageComponent,
    PptScreenComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'web-ppt-portal';
}
