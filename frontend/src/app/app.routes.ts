import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { ChatComponent } from './features/chat/chat.component';
import { UploadComponent } from './features/upload/upload.component';
import { AnalyticsComponent } from './features/analytics/analytics.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'chat', component: ChatComponent, canActivate: [authGuard] },
  { path: 'upload', component: UploadComponent, canActivate: [authGuard] },
  { path: 'analytics', component: AnalyticsComponent, canActivate: [adminGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' }
];
