import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UploadService } from '../../core/services/upload.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>Admin Upload</h2>
      <button class="secondary" (click)="bootstrap()">Bootstrap Demo Docs</button>
      <p class="small" *ngIf="bootstrapMessage">{{ bootstrapMessage }}</p>
      <label>Document ID</label>
      <input [(ngModel)]="id" />
      <label>Title</label>
      <input [(ngModel)]="title" />
      <label>Source Type</label>
      <input [(ngModel)]="sourceType" />
      <label>Source URI</label>
      <input [(ngModel)]="sourceUri" />
      <label>Content</label>
      <textarea rows="8" [(ngModel)]="content"></textarea>
      <button (click)="upload()">Upload</button>
      <p class="small" *ngIf="uploadMessage">{{ uploadMessage }}</p>
    </div>
  `
})
export class UploadComponent {
  id = 'shipping-policy-demo';
  title = 'Shipping Policy';
  sourceType = 'manual';
  sourceUri = 'admin-panel';
  content = 'Orders above $50 qualify for free shipping. Express shipping takes 1 to 2 business days.';
  bootstrapMessage = '';
  uploadMessage = '';

  constructor(private uploadService: UploadService) {}

  bootstrap() {
    this.uploadService.bootstrap().subscribe(res => this.bootstrapMessage = JSON.stringify(res));
  }

  upload() {
    this.uploadService.upload({
      id: this.id,
      title: this.title,
      sourceType: this.sourceType,
      sourceUri: this.sourceUri,
      content: this.content
    }).subscribe(res => this.uploadMessage = JSON.stringify(res));
  }
}
