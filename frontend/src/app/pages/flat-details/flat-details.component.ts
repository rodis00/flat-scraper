import { DecimalPipe, KeyValuePipe, TitleCasePipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavComponent } from '../../components/nav/nav.component';
import { IFlatFull } from '../../interfaces/flat-full';
import { FlatService } from '../../services/flat.service';

@Component({
  selector: 'app-home-details',
  templateUrl: './flat-details.component.html',
  styleUrl: './flat-details.component.css',
  imports: [NavComponent, TitleCasePipe, DecimalPipe, KeyValuePipe],
})
export class FlatDetailsComponent {
  flatService: FlatService = inject(FlatService);
  activeRoute: ActivatedRoute = inject(ActivatedRoute);

  flat!: IFlatFull;
  extraElements: string[] = [];
  flatDetails: Record<string, any> = {};

  constructor() {
    this.getFlatById();
  }

  getFlatById(): void {
    const flatId = this.activeRoute.snapshot.params['flatId'];
    this.flatService.getFlatById(flatId).subscribe({
      next: (flat: IFlatFull) => {
        this.flat = flat;
        this.extraElements = this.getExtraElements(this.flat);
        this.getFlatDetails(flat);
      },
      error: (error: any) => {
        console.error('Error fetching flat:', error);
      },
    });
  }

  getExtraElements(flat: IFlatFull): string[] {
    if (flat.additionalInfo) {
      return flat.additionalInfo.split(' ');
    }
    return [];
  }

  getFlatDetails(flat: IFlatFull): void {
    this.flatDetails = {
      'Forma właśności': flat.formOfOwnership,
      'Dostępne od': flat.availableFrom,
      'Materiał': flat.buildingMaterial,
      'Typ budynku': flat.buildingType,
      'Ogrzewanie': flat.heating,
      'Rynek': flat.market,
      'Rok budowy': flat.yearOfConstruction,
      'Czynsz': flat.rent > 0 ? flat.rent + ' zł' : null,
      'Winda': flat.elevator,
      'Stan wykończenia': flat.stateOfFinishing,
      'Certyfikat energetyczny': flat.energyCertificate,
      'Okna': flat.windows,
      'Media': flat.media,
      'Bezpieczeństwo': flat.safety,
      'Zabezpieczenia': flat.security,
    };
  }
}
