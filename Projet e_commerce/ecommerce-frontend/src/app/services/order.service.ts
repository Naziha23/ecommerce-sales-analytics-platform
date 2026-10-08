import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { Order, OrderStatus } from '../models/order';

import { getApiBaseUrl } from '../config/api.config';

export type { Order, OrderStatus };

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private apiUrl = `${getApiBaseUrl()}/orders`;
  private cache: Order[] | null = null;

  constructor(private http: HttpClient) { }

  getOrders(forceRefresh = false): Observable<Order[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<Order[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getOrder(id: number): Observable<Order> {
    if (this.cache) {
      const found = this.cache.find(o => o.id === id);
      if (found) return of(found);
    }
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  getOrdersByCustomer(customerId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  createOrder(order: Partial<Order>): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  updateOrderStatus(id: number, status: OrderStatus): Observable<Order> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<Order>(`${this.apiUrl}/${id}/status`, null, { params }).pipe(
      tap(updated => {
        if (this.cache) {
          const index = this.cache.findIndex(o => o.id === id);
          if (index !== -1) {
            this.cache[index] = updated;
          }
        }
      })
    );
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        if (this.cache) {
          this.cache = this.cache.filter(o => o.id !== id);
        }
      })
    );
  }

  clearCache() {
    this.cache = null;
  }
}
