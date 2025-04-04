import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  setToken(accessToken: string): void {
    localStorage.setItem('accessToken', accessToken);
  }
  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }
  removeToken(): void {
    localStorage.removeItem('accessToken');
  }
}
