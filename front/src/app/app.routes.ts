import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'assessment', pathMatch: 'full' },
  {
    path: 'assessment',
    loadComponent: () =>
      import('./pages/assessment/assessment-page').then((m) => m.AssessmentPage),
  },
  {
    path: 'history',
    loadComponent: () =>
      import('./pages/history/history-page').then((m) => m.HistoryPage),
  },
  {
    path: 'history/:id',
    loadComponent: () =>
      import('./pages/history/history-detail').then((m) => m.HistoryDetail),
  },
  {
    path: 'fraud',
    loadComponent: () =>
      import('./pages/fraud/fraud-page').then((m) => m.FraudPage),
  },
  { path: '**', redirectTo: 'assessment' },
];
