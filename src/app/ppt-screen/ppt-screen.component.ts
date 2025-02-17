import { Component } from '@angular/core';
import { NavbarComponent } from '../components/navbar/navbar.component';
import { MainscreenComponent } from '../components/mainscreen/mainscreen.component';
import { SidebarComponent } from '../components/sidebar/sidebar.component';


@Component({
  selector: 'app-ppt-screen',
  imports: [NavbarComponent,
      MainscreenComponent,
      SidebarComponent],
  templateUrl: './ppt-screen.component.html',
  styleUrl: './ppt-screen.component.css'
})
export class PptScreenComponent {

}
