import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QaService, AskResponse } from '../../core/services/qa.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>Support Chat</h2>
      <label>Customer Tier</label>
      <select [(ngModel)]="customerTier">
        <option value="standard">standard</option>
        <option value="premium">premium</option>
      </select>
      <label>Question</label>
      <textarea rows="5" [(ngModel)]="question"></textarea>
      <button (click)="ask()">Ask</button>
    </div>

    <div class="card" *ngIf="response">
      <h3>Answer</h3>
      <p>{{ response.answer }}</p>
      <p class="small">Confidence: {{ response.confidence | number:'1.2-2' }} · Latency: {{ response.latencyMs }} ms</p>
      <h4>Citations</h4>
      <ul>
        <li *ngFor="let c of response.citations">{{ c.title }} ({{ c.documentId }})</li>
      </ul>
    </div>
  `
})
export class ChatComponent {
  question = 'What is the return policy for electronics?';
  customerTier = 'standard';
  response: AskResponse | null = null;

  constructor(private qa: QaService) {}

  ask() {
    this.qa.ask({ question: this.question, customerTier: this.customerTier }).subscribe(res => this.response = res);
  }
}
