import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>Login</h2>
      <p class="small">Demo users: admin&#64;demo.com / admin123, support&#64;demo.com / support123, customer&#64;demo.com / customer123</p>
      <label>Email</label>
      <input [(ngModel)]="email" />
      <label>Password</label>
      <input type="password" [(ngModel)]="password" />
      <button (click)="login()">Login</button>
      <p *ngIf="error" class="small">{{ error }}</p>
    </div>
  `
})
export class LoginComponent {
  email = 'admin@demo.com';
  password = 'admin123';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.error = '';
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: () => this.router.navigate(['/chat']),
      error: err => this.error = err?.error?.message ?? 'Login failed'
    });
  }
}
