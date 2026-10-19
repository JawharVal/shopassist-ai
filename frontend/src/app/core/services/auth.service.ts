import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface LoginRequest { email: string; password: string; }
export interface LoginResponse { token: string; email: string; tenantId: string; role: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'shopassist_token';
  private readonly userKey = 'shopassist_user';
  private readonly api = 'http://localhost:8081/api';

  constructor(private http: HttpClient, private router: Router) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/auth/login`, body).pipe(
      tap(res => {
        localStorage.setItem(this.tokenKey, res.token);
        localStorage.setItem(this.userKey, JSON.stringify(res));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }

  token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  currentUser(): LoginResponse | null {
    const raw = localStorage.getItem(this.userKey);
    return raw ? JSON.parse(raw) as LoginResponse : null;
  }

  isAuthenticated(): boolean { return !!this.token(); }
  isAdmin(): boolean { return this.currentUser()?.role === 'ADMIN'; }
  canUpload(): boolean { return ['ADMIN', 'SUPPORT'].includes(this.currentUser()?.role ?? ''); }
}
