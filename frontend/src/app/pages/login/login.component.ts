import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ErrorInterface } from '../../interfaces/error';
import { TokenInterface } from '../../interfaces/token';
import { AuthService } from '../../services/auth.service';
import { TokenService } from '../../services/token.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  authService: AuthService = inject(AuthService);
  tokenService: TokenService = inject(TokenService);
  router: Router = inject(Router);

  loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  usernameError: string | null = null;
  passwordError: string | null = null;

  onSubmit(): void {
    this.clearErrors();
    this.authService
      .login({
        username: this.loginForm.value.username!,
        password: this.loginForm.value.password!,
      })
      .subscribe({
        next: (response) => {
          const token: TokenInterface = response;
          this.tokenService.setToken(token.accessToken);
          this.router.navigate(['']);
        },
        error: (error) => {
          const err: ErrorInterface = error.error;
          if (err.error === 'username') {
            this.usernameError = err.message;
          }
          if (err.error === 'password') {
            this.passwordError = err.message;
          }
          console.log(error);
        },
      });
  }

  clearErrors(): void {
    this.usernameError = null;
    this.passwordError = null;
  }
}
