import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import {
  AssessmentRecord,
  AssessmentRequest,
  AssessmentResponse,
  FraudCheckRequest,
  FraudCheckResponse,
  ProcenaSteteEvent,
} from '../models/types';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl;

  evaluate(request: AssessmentRequest): Observable<AssessmentResponse> {
    return this.http.post<AssessmentResponse>(`${this.base}/assessments/evaluate`, request);
  }

  listAssessments(): Observable<AssessmentRecord[]> {
    return this.http.get<AssessmentRecord[]>(`${this.base}/assessments`);
  }

  findAssessmentsByChassis(brojSasije: string): Observable<AssessmentRecord[]> {
    return this.http.get<AssessmentRecord[]>(
      `${this.base}/assessments/chassis/${encodeURIComponent(brojSasije)}`,
    );
  }

  getAssessment(id: number): Observable<AssessmentRecord> {
    return this.http.get<AssessmentRecord>(`${this.base}/assessments/${id}`);
  }

  fraudCheck(request: FraudCheckRequest): Observable<FraudCheckResponse> {
    return this.http.post<FraudCheckResponse>(`${this.base}/fraud/check`, request);
  }

  fraudEvent(event: ProcenaSteteEvent): Observable<FraudCheckResponse> {
    return this.http.post<FraudCheckResponse>(`${this.base}/fraud/event`, event);
  }

  getBrands(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/options/brands`);
  }

  getCarParts(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/options/car-parts`);
  }
}
