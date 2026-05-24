import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AssessmentRecord } from '../../models/types';

@Component({
  selector: 'app-history-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './history-detail.html',
})
export class HistoryDetail {
  private route = inject(ActivatedRoute);
  private api = inject(ApiService);

  loading = signal(true);
  errorMsg = signal<string | null>(null);
  record = signal<AssessmentRecord | null>(null);

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.errorMsg.set('Neispravan ID.');
      this.loading.set(false);
      return;
    }
    this.api.getAssessment(id).subscribe({
      next: (r) => {
        this.record.set(r);
        this.loading.set(false);
      },
      error: (err) => {
        const status = err?.status;
        this.errorMsg.set(
          status === 404 ? 'Procena nije pronađena.' : err?.message ?? 'Greška pri preuzimanju.',
        );
        this.loading.set(false);
      },
    });
  }

  decisionBadge(odluka: string | null): string {
    if (!odluka) return 'badge';
    return odluka === 'TOTALNA_STETA' ? 'badge badge-danger' : 'badge badge-success';
  }
}
