import { DecimalPipe, TitleCasePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavComponent } from '../../components/nav/nav.component';
import { IFlatFull } from '../../interfaces/flat-full';
import { FlatService } from '../../services/flat.service';

@Component({
  selector: 'app-home-details',
  templateUrl: './flat-details.component.html',
  styleUrl: './flat-details.component.css',
  imports: [NavComponent, TitleCasePipe, DecimalPipe],
})
export class FlatDetailsComponent {
  flatService: FlatService = inject(FlatService);
  activeRoute: ActivatedRoute = inject(ActivatedRoute);

  flat!: IFlatFull;
  extraElements: string[] = [];

  referralPercent: number = 0;
  suggestedPrice: number = 0;

  constructor() {
    this.getFlatById();
  }

  getFlatById(): void {
    const flatId = this.activeRoute.snapshot.params['flatId'];
    this.flatService.getFlatById(flatId).subscribe({
      next: (flat: IFlatFull) => {
        this.flat = flat;
        this.extraElements = this.getExtraElements(this.flat);
        this.predictPrice();
      },
      error: (error: any) => {
        console.error('Error fetching flat:', error);
      },
    });
  }

  getExtraElements(flat: IFlatFull): string[] {
    if (flat.additionalInfo) {
      console.log(flat.additionalInfo.split(' '));
      return flat.additionalInfo.split(' ');
    }
    return [];
  }

  predictPrice(): void {
    this.flatService.predictPrice(this.flat).subscribe({
      next: (response: number) => {
        this.suggestedPrice = response;
        this.calculateReferralPercent();
      },
      error: (error: any) => {
        console.error('Error predicting price:', error);
      },
    });
  }

  calculateReferralPercent(): void {
    const originalPrice = this.flat.price;
    const suggestedPrice = this.suggestedPrice;

    this.referralPercent =
      ((suggestedPrice - originalPrice) / suggestedPrice) * 100;
  }
}
