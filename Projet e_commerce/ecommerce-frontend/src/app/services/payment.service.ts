import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { Payment } from '../models/payment';

import { getApiBaseUrl } from '../config/api.config';

export type { Payment };

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private apiUrl = `${getApiBaseUrl()}/payments`;
  private cache: Payment[] | null = null;

  constructor(private http: HttpClient) { }

  getPayments(forceRefresh = false): Observable<Payment[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<Payment[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getPayment(id: number): Observable<Payment> {
    if (this.cache) {
      const found = this.cache.find(p => p.id === id);
      if (found) return of(found);
    }
    return this.http.get<Payment>(`${this.apiUrl}/${id}`);
  }

  getPaymentByOrder(orderId: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/order/${orderId}`);
  }

  createPayment(payment: Partial<Payment>): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, payment).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        if (this.cache) {
          this.cache = this.cache.filter(p => p.id !== id);
        }
      })
    );
  }

  clearCache() {
    this.cache = null;
  }
}
