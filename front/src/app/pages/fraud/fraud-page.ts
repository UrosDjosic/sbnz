import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { ApiService } from '../../services/api.service';
import { FraudAlert, FraudCheckRequest, TipEskalacije, ZahtevEvent } from '../../models/types';

@Component({
  selector: 'app-fraud-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './fraud-page.html',
})
export class FraudPage {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);

  loading = signal(false);
  errorMsg = signal<string | null>(null);
  alerts = signal<FraudAlert[] | null>(null);
  lastSubmittedCount = signal(0);

  form: FormGroup = this.fb.group({
    zahtevi: this.fb.array([this.createClaim()]),
  });

  get zahtevi(): FormArray {
    return this.form.get('zahtevi') as FormArray;
  }

  private nowLocal(offsetDays = 0): string {
    const d = new Date();
    d.setDate(d.getDate() + offsetDays);
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(
      d.getHours(),
    )}:${pad(d.getMinutes())}`;
  }

  createClaim(initial?: Partial<ZahtevEvent>): FormGroup {
    return this.fb.group({
      vlasnikId: [initial?.vlasnikId ?? 'OWNER-1', Validators.required],
      voziloBroj: [initial?.voziloBroj ?? 'BG-001-AA', Validators.required],
      iznosZahteva: [initial?.iznosZahteva ?? 1500, [Validators.required, Validators.min(0)]],
      vremeZahteva: [initial?.vremeZahteva ?? this.nowLocal(), Validators.required],
    });
  }

  addClaim(): void {
    this.zahtevi.push(this.createClaim());
  }

  removeClaim(i: number): void {
    if (this.zahtevi.length > 1) this.zahtevi.removeAt(i);
  }

  fillExample(): void {
    this.zahtevi.clear();
    this.zahtevi.push(
      this.createClaim({
        vlasnikId: 'OWNER-42',
        voziloBroj: 'NS-123-CD',
        iznosZahteva: 2200,
        vremeZahteva: this.nowLocal(-30),
      }),
    );
    this.zahtevi.push(
      this.createClaim({
        vlasnikId: 'OWNER-42',
        voziloBroj: 'NS-123-CD',
        iznosZahteva: 1800,
        vremeZahteva: this.nowLocal(-12),
      }),
    );
    this.zahtevi.push(
      this.createClaim({
        vlasnikId: 'OWNER-42',
        voziloBroj: 'NS-123-CD',
        iznosZahteva: 2600,
        vremeZahteva: this.nowLocal(-3),
      }),
    );
  }

  badgeClass(nivo: TipEskalacije): string {
    switch (nivo) {
      case 'ESKALACIJA':
        return 'badge badge-danger';
      case 'STRUCNA_PROVERA':
        return 'badge badge-warning';
      default:
        return 'badge badge-info';
    }
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMsg.set('Molimo popunite sva polja ispravno.');
      return;
    }
    this.errorMsg.set(null);
    this.loading.set(true);

    const raw = this.form.getRawValue() as { zahtevi: { vlasnikId: string; voziloBroj: string; iznosZahteva: number; vremeZahteva: string }[] };
    const payload: FraudCheckRequest = {
      zahtevi: raw.zahtevi.map((z) => ({
        vlasnikId: z.vlasnikId,
        voziloBroj: z.voziloBroj,
        iznosZahteva: Number(z.iznosZahteva),
        vremeZahteva: new Date(z.vremeZahteva).toISOString(),
      })),
    };

    this.lastSubmittedCount.set(payload.zahtevi.length);

    this.api.fraudCheck(payload).subscribe({
      next: (res) => {
        this.alerts.set(res.alerti ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMsg.set(
          err?.error?.message ??
            err?.message ??
            'Greška pri komunikaciji sa serverom. Proverite da li backend radi.',
        );
        this.loading.set(false);
      },
    });
  }
}
