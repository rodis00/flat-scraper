import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class ChatService {

    url: string = 'http://localhost:8080/api/v1/chat/predict-price';
    http: HttpClient = inject(HttpClient);
    tokenService: TokenService = inject(TokenService);
    accessToken: string | null;
    headers: HttpHeaders;

  constructor() {
    this.accessToken = this.tokenService.getToken();
    this.headers = new HttpHeaders({
      Authorization: `Bearer ${this.accessToken}`,
      'Content-Type': 'application/json',
    });
  }

  predictPrice(payload: { message: string }) {
    return this.http.post<{ response: string }>(
      this.url,
      payload,
      {
        headers: this.headers
      }
    );
  }
}
