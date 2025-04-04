import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-sort-options',
  templateUrl: './sort-options.component.html',
})
export class SortOptionsComponent {
  @Input() selectedOption: string | null = null;
  @Output() sortChanged = new EventEmitter<string>();

  onSortChange(event: any) {
    this.sortChanged.emit(event.target.value);
  }
}
