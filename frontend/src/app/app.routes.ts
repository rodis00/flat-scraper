import { Routes } from '@angular/router';
import { authGuard } from './guards/auth/auth.guard';
import { isLoggedInGuard } from './guards/loggedIn/is-logged-in.guard';
import { FlatDetailsComponent } from './pages/flat-details/flat-details.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { PricingComponent } from './pages/pricing/pricing.component';

export const routes: Routes = [
  {
    path: '',
    title: 'Flat Scraper',
    component: HomeComponent,
    canActivate: [authGuard],
  },
  {
    path: 'flats/flat/:flatId',
    title: 'Flat Details',
    component: FlatDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'pricing',
    title: 'Pricing',
    component: PricingComponent,
    canActivate: [authGuard]
  },
  {
    path: 'auth/login',
    title: 'Login',
    component: LoginComponent,
    canActivate: [isLoggedInGuard],
  },
  {
    path: 'auth/register',
    title: 'Register',
    component: RegisterComponent,
    canActivate: [isLoggedInGuard],
  },
  {
    path: '**', // redirect for unknow paths
    redirectTo: '',
  },
];
