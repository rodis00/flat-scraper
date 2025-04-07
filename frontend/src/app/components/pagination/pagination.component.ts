import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent {
  currPage: number = 0;
  @Input() firstPage!: boolean;
  @Input() lastPage!: boolean;
  @Input() numberOfElements!: number;

  @Output() pageNumberEvent = new EventEmitter<number>();

  increasePage(): void {
    this.currPage++;
    this.sendEvent();
  }

  decreasePage(): void {
    this.currPage--;
    this.sendEvent();
  }

  sendEvent(): void {
    this.pageNumberEvent.emit(this.currPage);
  }
}
