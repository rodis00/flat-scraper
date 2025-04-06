import { DecimalPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { IFlatShort } from '../../interfaces/flat-short';

@Component({
  selector: 'app-flat',
  templateUrl: './flat.component.html',
  styleUrls: ['./flat.component.css'],
  imports: [DecimalPipe],
})
export default class FlatComponent {
  @Input() flat!: IFlatShort;
}
