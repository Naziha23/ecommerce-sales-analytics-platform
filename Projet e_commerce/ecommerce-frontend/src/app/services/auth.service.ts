import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/login-response';

export interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://127.0.0.1:8081/api/auth';
  private readonly credentialsKey = 'ecommerce.auth.credentials';
  private readonly userKey = 'ecommerce.auth.user';
  private readonly storage: Storage | null = typeof sessionStorage === 'undefined' ? null : sessionStorage;
  readonly isAuthenticated = signal(this.hasCredentials());

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        const credentials = btoa(`${request.username}:${request.password}`);
        this.storage?.setItem(this.credentialsKey, credentials);
        this.storage?.setItem(this.userKey, JSON.stringify(response));
        this.isAuthenticated.set(true);
      })
    );
  }

  logout(): void {
    this.storage?.removeItem(this.credentialsKey);
    this.storage?.removeItem(this.userKey);
    this.isAuthenticated.set(false);
  }

  getAuthorizationHeader(): string | null {
    const credentials = this.storage?.getItem(this.credentialsKey);
    return credentials ? `Basic ${credentials}` : null;
  }

  getCurrentUser(): LoginResponse | null {
    const user = this.storage?.getItem(this.userKey);
    return user ? JSON.parse(user) as LoginResponse : null;
  }

  private hasCredentials(): boolean {
    return !!this.storage?.getItem(this.credentialsKey);
  }
}
