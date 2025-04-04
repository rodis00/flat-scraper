import { DecimalPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FlatInterface } from '../../interfaces/flat';

@Component({
  selector: 'app-flat',
  templateUrl: './flat.component.html',
  styleUrls: ['./flat.component.css'],
  imports: [DecimalPipe],
})
export default class FlatComponent {
  @Input() flat!: FlatInterface;
}
