import { Component, signal } from '@angular/core';
import { FlatsComponent } from '../../components/flats/flats.component';
import { NavComponent } from '../../components/nav/nav.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [NavComponent, FlatsComponent],
})
export class HomeComponent {
  searchBoxValue = signal('');
}
