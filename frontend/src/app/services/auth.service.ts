import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginData } from '../interfaces/login-data';
import { RegisterData } from '../interfaces/register-data';
import { TokenInterface } from '../interfaces/token';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  tokenService: TokenService = inject(TokenService);
  http: HttpClient = inject(HttpClient);
  router: Router = inject(Router);

  apiUrl: string = 'http://localhost:8080/api/v1/auth/authenticate';

  login(loginData: LoginData): Observable<TokenInterface> {
    return this.http.post<TokenInterface>(this.apiUrl, loginData);
  }

  logout(): void {
    this.tokenService.removeToken();
    this.router.navigate(['/auth/login']);
  }

  register(RegisterData: RegisterData): void {}

  isLoggedIn(): boolean {
    return this.tokenService.getToken() !== null;
  }

  getUsername(): string {
    const payload = this.tokenService.extractPayloadFromToken();
    if (payload) {
      return payload.sub;
    }
    return 'user';
  }
}
