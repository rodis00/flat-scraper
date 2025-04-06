import { HttpClient } from '@angular/common/http';
import { Component, effect, inject, Input, Signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IFlatShort } from '../../interfaces/flat-short';
import { FlatService } from '../../services/flat.service';
import FlatComponent from '../flat/flat.component';
import { SortOptionsComponent } from '../sort/sort-options.component';

@Component({
  selector: 'app-flats',
  templateUrl: './flats.component.html',
  imports: [FlatComponent, SortOptionsComponent, RouterLink],
})
export class FlatsComponent {
  httpClient: HttpClient = inject(HttpClient);
  flatService: FlatService = inject(FlatService);

  flats: IFlatShort[] = [];
  selectedOption: string | null = null;
  @Input() searchBoxValue!: Signal<string>;

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

    this.flatService.getFlats(queryParams).subscribe({
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
