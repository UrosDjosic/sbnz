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
import {
  AssessmentRecord,
  FraudAlert,
  FraudCheckRequest,
  ProcenaSteteEvent,
  TipEskalacije,
} from '../../models/types';

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
  lookupLoading = signal(false);
  errorMsg = signal<string | null>(null);
  alerts = signal<FraudAlert[] | null>(null);
  foundAssessments = signal<AssessmentRecord[]>([]);
  lastSubmittedCount = signal(0);

  form: FormGroup = this.fb.group({
    brojSasije: ['WBAKS410500C12345', Validators.required],
    procene: this.fb.array([]),
  });

  get procene(): FormArray {
    return this.form.get('procene') as FormArray;
  }

  lookupByChassis(): void {
    const brojSasije = String(this.form.get('brojSasije')?.value ?? '').trim();
    if (!brojSasije) {
      this.errorMsg.set('Unesite broj šasije.');
      return;
    }

    this.lookupLoading.set(true);
    this.errorMsg.set(null);
    this.foundAssessments.set([]);

    this.api.findAssessmentsByChassis(brojSasije).subscribe({
      next: (records) => {
        this.foundAssessments.set(records);
        this.lookupLoading.set(false);
      },
      error: (err) => {
        this.errorMsg.set(err?.message ?? 'Greška pri pretrazi procena.');
        this.lookupLoading.set(false);
      },
    });
  }

  addRecord(record: AssessmentRecord): void {
    const alreadyAdded = this.procene.controls.some(
      (ctrl) => ctrl.value.sourceId === record.id,
    );
    if (alreadyAdded) return;

    this.procene.push(this.createAssessmentEvent(this.eventFromRecord(record), record.id));
    this.alerts.set(null);
  }

  addAllFound(): void {
    for (const record of this.foundAssessments()) {
      this.addRecord(record);
    }
  }

  removeAssessmentEvent(i: number): void {
    this.procene.removeAt(i);
  }

  private eventFromRecord(record: AssessmentRecord): ProcenaSteteEvent {
    return {
      brojSasije: record.brojSasije,
      marka: record.marka,
      model: record.model,
      vremeProcene: record.kreirano,
      vrednostVozila: record.vrednostVozila,
      ukupniTroskovi: record.ukupniTroskovi,
      procenatStete: record.procenatStete,
      tipOdluke: record.odluka,
      brojOstecenihDelova: record.brojOstecenihDelova,
      imaKriticniSklop: record.imaKriticniSklop,
      naknada: record.naknada,
    };
  }

  private createAssessmentEvent(event: ProcenaSteteEvent, sourceId: number): FormGroup {
    return this.fb.group({
      sourceId: [sourceId],
      brojSasije: [event.brojSasije, Validators.required],
      marka: [event.marka, Validators.required],
      model: [event.model, Validators.required],
      vremeProcene: [event.vremeProcene, Validators.required],
      vrednostVozila: [event.vrednostVozila, [Validators.required, Validators.min(1)]],
      ukupniTroskovi: [event.ukupniTroskovi, [Validators.required, Validators.min(0)]],
      procenatStete: [event.procenatStete, [Validators.required, Validators.min(0)]],
      tipOdluke: [event.tipOdluke, Validators.required],
      brojOstecenihDelova: [event.brojOstecenihDelova, [Validators.required, Validators.min(0)]],
      imaKriticniSklop: [event.imaKriticniSklop],
      naknada: [event.naknada, [Validators.required, Validators.min(0)]],
    });
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
    if (this.procene.length === 0) {
      this.errorMsg.set('Dodajte bar jednu sačuvanu procenu za CEP proveru.');
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMsg.set('Izabrane procene nisu ispravne.');
      return;
    }

    this.errorMsg.set(null);
    this.loading.set(true);

    const raw = this.form.getRawValue() as { procene: Array<ProcenaSteteEvent & { sourceId: number }> };
    const payload: FraudCheckRequest = {
      procene: raw.procene.map(({ sourceId, ...p }) => ({
        ...p,
        vrednostVozila: Number(p.vrednostVozila),
        ukupniTroskovi: Number(p.ukupniTroskovi),
        procenatStete: Number(p.procenatStete),
        brojOstecenihDelova: Number(p.brojOstecenihDelova),
        naknada: Number(p.naknada),
      })),
    };

    this.lastSubmittedCount.set(payload.procene.length);

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
