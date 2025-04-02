import { Routes } from '@angular/router';
import { authGuard } from './guards/auth/auth.guard';
import { isLoggedInGuard } from './guards/loggedIn/is-logged-in.guard';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  {
    path: '',
    title: 'Home',
    component: HomeComponent,
    canActivate: [authGuard],
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
];
