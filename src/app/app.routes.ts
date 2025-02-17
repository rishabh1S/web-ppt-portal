import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { PptScreenComponent } from './ppt-screen/ppt-screen.component';

export const routes: Routes = [


    { path: '', component: LandingPageComponent }, 
    { path: 'ppt-screen', component: PptScreenComponent }

];
