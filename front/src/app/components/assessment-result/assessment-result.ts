import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AssessmentResponse, TipEskalacije, TipOdluke } from '../../models/types';

@Component({
  selector: 'app-assessment-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './assessment-result.html',
  styleUrl: './assessment-result.css',
})
export class AssessmentResult {
  @Input({ required: true }) response!: AssessmentResponse;

  decisionBadge(tip: TipOdluke | undefined | null): string {
    if (!tip) return 'badge';
    return tip === 'TOTALNA_STETA' ? 'badge badge-danger' : 'badge badge-success';
  }

  escalationBadge(nivo: TipEskalacije): string {
    switch (nivo) {
      case 'ESKALACIJA':
        return 'badge badge-danger';
      case 'STRUCNA_PROVERA':
        return 'badge badge-warning';
      default:
        return 'badge badge-info';
    }
  }
}
