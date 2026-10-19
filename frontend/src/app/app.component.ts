import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="container">
      <div class="nav" *ngIf="auth.isAuthenticated()">
        <a routerLink="/chat">Chat</a>
        <a routerLink="/upload" *ngIf="auth.canUpload()">Upload</a>
        <a routerLink="/analytics" *ngIf="auth.isAdmin()">Analytics</a>
        <span class="badge">{{ auth.currentUser()?.email }}</span>
        <span class="badge">{{ auth.currentUser()?.role }}</span>
        <button class="secondary" style="width:auto" (click)="logout()">Logout</button>
      </div>
      <router-outlet />
    </div>
  `
})
export class AppComponent {
  auth = inject(AuthService);
  router = inject(Router);

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
