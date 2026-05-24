import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AssessmentRecord } from '../../models/types';

@Component({
  selector: 'app-history-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './history-page.html',
})
export class HistoryPage {
  private api = inject(ApiService);

  loading = signal(true);
  errorMsg = signal<string | null>(null);
  records = signal<AssessmentRecord[]>([]);

  constructor() {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.api.listAssessments().subscribe({
      next: (data) => {
        const sorted = [...data].sort((a, b) =>
          (b.kreirano ?? '').localeCompare(a.kreirano ?? ''),
        );
        this.records.set(sorted);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMsg.set(err?.message ?? 'Greška pri preuzimanju istorije.');
        this.loading.set(false);
      },
    });
  }

  badgeClass(odluka: string | null): string {
    if (!odluka) return 'badge';
    return odluka === 'TOTALNA_STETA' ? 'badge badge-danger' : 'badge badge-success';
  }
}
