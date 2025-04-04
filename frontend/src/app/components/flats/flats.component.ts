import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, effect, Input, Signal } from '@angular/core';
import { FlatInterface } from '../../interfaces/flat';
import { TokenService } from '../../services/token.service';
import FlatComponent from '../flat/flat.component';
import { SortOptionsComponent } from '../sort/sort-options.component';

@Component({
  selector: 'app-flats',
  templateUrl: './flats.component.html',
  imports: [FlatComponent, SortOptionsComponent],
})
export class FlatsComponent {
  flats: FlatInterface[] = [];
  selectedOption: string | null = null;
  @Input() searchBoxValue!: Signal<string>;

  accessToken: string | null;

  constructor(private http: HttpClient, private tokenService: TokenService) {
    this.accessToken = tokenService.getToken();
    effect(() => {
      this.onSearchChange();
    });
    this.fetchFlats();
    console.log(this.flats);
  }

  fetchFlats() {
    let url = 'http://localhost:8080/api/v1/flats';
    const queryParams: string[] = [];

    if (this.selectedOption) {
      queryParams.push(`sort=${this.selectedOption}`);
    }
    if (this.searchBoxValue) {
      queryParams.push(`search=${this.searchBoxValue()}`);
    }
    if (queryParams.length > 0) {
      url += `?${queryParams.join('&')}`;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.accessToken}`,
    });

    this.http.get<any>(url, { headers }).subscribe({
      next: (data) => {
        console.log(data);
        this.flats = data.content;
      },
      error: (error) => console.error(error),
    });
  }

  onSearchChange() {
    this.fetchFlats();
  }

  onSortChange(selectedOption: string) {
    this.selectedOption = selectedOption;
    this.fetchFlats();
  }
}
