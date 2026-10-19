import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AnalyticsSummary {
  totalQuestions: number;
  averageConfidence: number;
  averageLatencyMs: number;
  topQuestions: { question: string; count: number }[];
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly api = 'http://localhost:8081/api';
  constructor(private http: HttpClient) {}
  summary(): Observable<AnalyticsSummary> { return this.http.get<AnalyticsSummary>(`${this.api}/analytics/summary`); }
}
