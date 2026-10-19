import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService, AnalyticsSummary } from '../../core/services/analytics.service';

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card" *ngIf="summary">
      <h2>Analytics</h2>
      <p><strong>Total questions:</strong> {{ summary.totalQuestions }}</p>
      <p><strong>Average confidence:</strong> {{ summary.averageConfidence | number:'1.2-2' }}</p>
      <p><strong>Average latency:</strong> {{ summary.averageLatencyMs | number:'1.0-0' }} ms</p>
      <h3>Top questions</h3>
      <ul>
        <li *ngFor="let q of summary.topQuestions">{{ q.question }} — {{ q.count }}</li>
      </ul>
    </div>
  `
})
export class AnalyticsComponent implements OnInit {
  summary: AnalyticsSummary | null = null;
  constructor(private analytics: AnalyticsService) {}
  ngOnInit(): void { this.analytics.summary().subscribe(res => this.summary = res); }
}
