import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { IFlatFull } from '../interfaces/flat-full';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root',
})
export class FlatService {
  http: HttpClient = inject(HttpClient);
  tokenService: TokenService = inject(TokenService);

  url: string = 'http://localhost:8080/api/v1/flats';
  accessToken: string | null;
  headers: HttpHeaders;

  constructor() {
    this.accessToken = this.tokenService.getToken();
    this.headers = new HttpHeaders({
      Authorization: `Bearer ${this.accessToken}`,
      'Content-Type': 'application/json',
    });
  }

  getFlats(queryParams: string[]) {
    let urlWithQueryParams = this.url;
    if (queryParams.length > 0) {
      urlWithQueryParams += `?${queryParams.join('&')}`;
    }
    return this.http.get<any>(urlWithQueryParams, {
      headers: this.headers,
    });
  }

  getFlatById(flatId: number) {
    return this.http.get<IFlatFull>(`${this.url}/flat/${flatId}`, {
      headers: this.headers,
    });
  }

  predictPrice(flat: IFlatFull) {
    const data = {
      area: flat.area,
      rooms: flat.rooms,
      floor: flat.floor,
      yearOfConstruction: flat.yearOfConstruction,
    };
    return this.http.post<any>(`${this.url}/predict-price`, data, {
      headers: this.headers,
    });
  }
}
