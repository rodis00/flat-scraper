import { HttpClient } from '@angular/common/http';
import { Component, effect, inject, Input, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IFlatShort } from '../../interfaces/flat-short';
import { FlatService } from '../../services/flat.service';
import FlatComponent from '../flat/flat.component';
import { PaginationComponent } from '../pagination/pagination.component';
import { SortOptionsComponent } from '../sort/sort-options.component';

@Component({
  selector: 'app-flats',
  templateUrl: './flats.component.html',
  imports: [
    FlatComponent,
    SortOptionsComponent,
    RouterLink,
    PaginationComponent,
  ],
})
export class FlatsComponent {
  httpClient: HttpClient = inject(HttpClient);
  flatService: FlatService = inject(FlatService);

  flats: IFlatShort[] = [];
  selectedOption: string | null = null;

  @Input() searchBoxValue!: Signal<string>;

  pageNumber!: number;
  numberOfElements!: number;

  firstPage!: boolean;
  lastPage!: boolean;

  constructor() {
    effect(() => {
      this.onSearchChange();
    });
    this.fetchFlats();
    console.log(this.flats);
  }

  fetchFlats(): void {
    const queryParams: string[] = [];

    if (this.selectedOption) {
      queryParams.push(`sort=${this.selectedOption}`);
    }
    if (this.searchBoxValue) {
      queryParams.push(`search=${this.searchBoxValue()}`);
    }
    if (this.pageNumber) {
      queryParams.push(`page=${this.pageNumber}`);
    }

    this.flatService.getFlats(queryParams).subscribe({
      next: (data) => {
        console.log(data);
        this.flats = data.content;
        this.firstPage = data.first;
        this.lastPage = data.last;
        this.numberOfElements = data.numberOfElements;
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

  setPageNumber(page: number) {
    this.pageNumber = page;
    this.fetchFlats();
  }
}
