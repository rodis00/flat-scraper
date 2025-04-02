import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
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

  apiUrl: string = 'http://localhost:8080/api/v1/auth/authenticate';

  login(loginData: LoginData): Observable<TokenInterface> {
    return this.http.post<TokenInterface>(this.apiUrl, loginData);
  }

  register(RegisterData: RegisterData): void {}
}
