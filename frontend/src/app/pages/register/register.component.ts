import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TokenInterface } from '../../interfaces/token';
import { TokenService } from '../../services/token.service';
import { ErrorInterface } from '../../interfaces/error';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  authService: AuthService = inject(AuthService);
  tokenService: TokenService = inject(TokenService);
  router: Router = inject(Router);

  usernameError: string | null = null;
  passwordError: string | null = null;
  emailError: string | null = null;

  registerForm = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(3),
    ]),
    email: new FormControl('', [Validators.email, Validators.required]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
  });

  getErrorMessage(field: string): string {
    const control = this.registerForm.get(field);
    if (control?.hasError('required')) {
      return 'This field is required.';
    }
    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength'].requiredLength;
      return `Min length is ${minLength} characters.`;
    }
    if (control?.hasError('email')) {
      return 'Invalid email address.';
    }
    return '';
  }

  onSubmit() {
    this.authService
      .register({
        username: this.registerForm.value.username!,
        password: this.registerForm.value.password!,
        email: this.registerForm.value.email!,
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
          if (err.error === 'email') {
            this.emailError = err.message;
          }
          console.log(error);
        },
      });
  }
}
