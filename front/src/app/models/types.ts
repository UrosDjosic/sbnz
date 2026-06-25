export type TipKoriscenja = 'PRIVATNO' | 'TAKSI' | 'RENT_A_CAR' | 'DOSTAVA';
export type KvalitetPopravki = 'NEMA' | 'LOS' | 'PROSECAN' | 'DOBAR';
export type TipIntervencije = 'ZAMENA' | 'POPRAVKA' | 'FARBANJE';
export type TipOdluke = 'TOTALNA_STETA' | 'EKONOMSKA_POPRAVKA';
export type TipFaktora = 'STAROST' | 'KM' | 'STANJE' | 'EKSPLOATACIJA' | 'BEZBEDNOST' | 'RANIJE_STETE';
export type TipIndikatora = 'KRITICNA_KOMPONENTA' | 'AIRBAG_AKTIVIRAN' | 'RANIJE_STETE';
export type TipEskalacije = 'STRUCNA_PROVERA' | 'PROVERA' | 'ESKALACIJA';

export interface Vozilo {
  marka: string;
  model: string;
  godinaPrveRegistracije: number;
  predjenoKm: number;
  prosecnoKmKategorije: number;
  nabavnaCena: number;
  stanje: number;
  tipKoriscenja: TipKoriscenja;
  ranijeStete: boolean;
  kvalitetPrethodnih: KvalitetPopravki;
  airbagAktiviran: boolean;
}

export interface OstecenoVozilo {
  naziv: string;
  tipIntervencije: TipIntervencije;
  cenaDela: number;
  normaSati: number;
  cenaRadaSat: number;
  procenatOstecenja: number;
}

export interface AssessmentRequest {
  brojSasije: string;
  vozilo: Vozilo;
  ostecenja: OstecenoVozilo[];
}

export interface Faktor {
  tip: TipFaktora;
  koeficijent: number;
}

export interface Trosak {
  deo: string;
  iznos: number;
  tip: TipIntervencije;
}

export interface Indikator {
  tip: TipIndikatora;
  deo: string;
}

export interface Eskalacija {
  tip: TipEskalacije;
  opis: string;
}

export interface PromenaIntervencije {
  deo: string;
  od: TipIntervencije;
  ka: TipIntervencije;
  razlog: string;
}

export interface Odluka {
  tip: TipOdluke;
  naknada: number;
  obrazlozenje: string;
}

export interface AssessmentResponse {
  vrednostVozila: number;
  vrednostOstataka: number;
  ukupniTroskovi: number;
  odluka: Odluka | null;
  faktori: Faktor[];
  troskovi: Trosak[];
  indikatori: Indikator[];
  eskalacije: Eskalacija[];
  promene: PromenaIntervencije[];
  fraudAlerti: FraudAlert[];
}

export interface AssessmentRecord {
  id: number;
  brojSasije: string;
  marka: string;
  model: string;
  nabavnaCena: number;
  vrednostVozila: number;
  vrednostOstataka: number;
  ukupniTroskovi: number;
  procenatStete: number;
  brojOstecenihDelova: number;
  imaKriticniSklop: boolean;
  naknada: number;
  odluka: TipOdluke | null;
  obrazlozenje: string | null;
  kreirano: string;
}

export interface ProcenaSteteEvent {
  brojSasije: string;
  marka: string;
  model: string;
  vremeProcene: string;
  vrednostVozila: number;
  ukupniTroskovi: number;
  procenatStete: number;
  tipOdluke: TipOdluke | null;
  brojOstecenihDelova: number;
  imaKriticniSklop: boolean;
  naknada: number;
}

export interface FraudAlert {
  brojSasije: string;
  opis: string;
  nivo: TipEskalacije;
}

export interface FraudCheckRequest {
  procene: ProcenaSteteEvent[];
}

export interface FraudCheckResponse {
  alerti: FraudAlert[];
}
