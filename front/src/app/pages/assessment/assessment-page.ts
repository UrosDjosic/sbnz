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
  AssessmentRequest,
  AssessmentResponse,
  KvalitetPopravki,
  OstecenoVozilo,
  TipIntervencije,
  TipKoriscenja,
  Vozilo,
} from '../../models/types';
import { AssessmentResult } from '../../components/assessment-result/assessment-result';

@Component({
  selector: 'app-assessment-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AssessmentResult,
  ],
  templateUrl: './assessment-page.html',
  styleUrl: './assessment-page.css',
})
export class AssessmentPage {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);

  readonly tipKoriscenjaOptions: TipKoriscenja[] = ['PRIVATNO', 'TAKSI', 'RENT_A_CAR', 'DOSTAVA'];
  readonly kvalitetOptions: KvalitetPopravki[] = ['NEMA', 'LOS', 'PROSECAN', 'DOBAR'];
  readonly tipIntervencijeOptions: TipIntervencije[] = ['ZAMENA', 'POPRAVKA', 'FARBANJE'];

  brandOptions = signal<string[]>([]);
  carPartOptions = signal<string[]>([]);

  loading = signal(false);
  errorMsg = signal<string | null>(null);
  result = signal<AssessmentResponse | null>(null);

  form: FormGroup = this.fb.group({
    vozilo: this.fb.group({
      marka: ['Kia', [Validators.required]],
      model: ['Ceed', [Validators.required]],
      godinaPrveRegistracije: [
        2018,
        [Validators.required, Validators.min(1980), Validators.max(new Date().getFullYear())],
      ],
      predjenoKm: [120000, [Validators.required, Validators.min(0)]],
      prosecnoKmKategorije: [15000, [Validators.required, Validators.min(1)]],
      nabavnaCena: [18000, [Validators.required, Validators.min(0)]],
      stanje: [3, [Validators.required, Validators.min(1), Validators.max(5)]],
      tipKoriscenja: ['PRIVATNO' as TipKoriscenja, Validators.required],
      ranijeStete: [false],
      kvalitetPrethodnih: ['NEMA' as KvalitetPopravki, Validators.required],
      airbagAktiviran: [false],
    }),
    ostecenja: this.fb.array([this.createDamage()]),
  });

  constructor() {
    this.loadBrands();
    this.loadCarParts();
  }

  get vozilo(): FormGroup {
    return this.form.get('vozilo') as FormGroup;
  }

  get ostecenja(): FormArray {
    return this.form.get('ostecenja') as FormArray;
  }

  private loadBrands(): void {
    this.api.getBrands().subscribe({
      next: (brands) => {
        this.brandOptions.set(brands);
      },
      error: (err) => {
        console.error('Error loading brands:', err);
      },
    });
  }

  private loadCarParts(): void {
    this.api.getCarParts().subscribe({
      next: (parts) => {
        this.carPartOptions.set(parts);
      },
      error: (err) => {
        console.error('Error loading car parts:', err);
      },
    });
  }

  createDamage(initial?: Partial<OstecenoVozilo>): FormGroup {
    return this.fb.group({
      naziv: [initial?.naziv ?? 'branik_prednji', Validators.required],
      tipIntervencije: [initial?.tipIntervencije ?? ('ZAMENA' as TipIntervencije), Validators.required],
      cenaDela: [initial?.cenaDela ?? 500, [Validators.required, Validators.min(0)]],
      normaSati: [initial?.normaSati ?? 4, [Validators.required, Validators.min(0)]],
      cenaRadaSat: [initial?.cenaRadaSat ?? 25, [Validators.required, Validators.min(0)]],
      procenatOstecenja: [
        initial?.procenatOstecenja ?? 50,
        [Validators.required, Validators.min(0), Validators.max(100)],
      ],
    });
  }

  addDamage(): void {
    this.ostecenja.push(this.createDamage());
  }

  removeDamage(index: number): void {
    if (this.ostecenja.length > 1) {
      this.ostecenja.removeAt(index);
    }
  }

  fillExample(): void {
    this.form.patchValue({
      vozilo: {
        marka: 'BMW',
        model: 'X5',
        godinaPrveRegistracije: 2016,
        predjenoKm: 220000,
        prosecnoKmKategorije: 15000,
        nabavnaCena: 60000,
        stanje: 2,
        tipKoriscenja: 'TAKSI',
        ranijeStete: true,
        kvalitetPrethodnih: 'LOS',
        airbagAktiviran: true,
      },
    });
    this.ostecenja.clear();
    this.ostecenja.push(
      this.createDamage({
        naziv: 'hauba',
        tipIntervencije: 'ZAMENA',
        cenaDela: 1200,
        normaSati: 6,
        cenaRadaSat: 30,
        procenatOstecenja: 75,
      }),
    );
    this.ostecenja.push(
      this.createDamage({
        naziv: 'motor',
        tipIntervencije: 'POPRAVKA',
        cenaDela: 4500,
        normaSati: 20,
        cenaRadaSat: 35,
        procenatOstecenja: 65,
      }),
    );
    this.ostecenja.push(
      this.createDamage({
        naziv: 'vetrobran',
        tipIntervencije: 'ZAMENA',
        cenaDela: 300,
        normaSati: 2,
        cenaRadaSat: 25,
        procenatOstecenja: 100,
      }),
    );
  }

  resetForm(): void {
    this.result.set(null);
    this.errorMsg.set(null);
    this.ostecenja.clear();
    this.ostecenja.push(this.createDamage());
    this.form.reset({
      vozilo: {
        marka: 'Kia',
        model: 'Ceed',
        godinaPrveRegistracije: 2018,
        predjenoKm: 120000,
        prosecnoKmKategorije: 15000,
        nabavnaCena: 18000,
        stanje: 3,
        tipKoriscenja: 'PRIVATNO',
        ranijeStete: false,
        kvalitetPrethodnih: 'NEMA',
        airbagAktiviran: false,
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMsg.set('Molimo popunite sva obavezna polja ispravno.');
      return;
    }
    this.errorMsg.set(null);
    this.loading.set(true);
    const payload = this.form.getRawValue() as AssessmentRequest;
    this.api.evaluate(payload).subscribe({
      next: (res) => {
        this.result.set(res);
        this.loading.set(false);
        setTimeout(() => {
          const el = document.getElementById('result-section');
          el?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }, 50);
      },
      error: (err) => {
        const msg =
          err?.error?.message ??
          err?.message ??
          'Greška pri komunikaciji sa serverom. Proverite da li backend radi na portu 8080.';
        this.errorMsg.set(msg);
        this.loading.set(false);
      },
    });
  }

  asGroup(ctrl: unknown): FormGroup {
    return ctrl as FormGroup;
  }
}
