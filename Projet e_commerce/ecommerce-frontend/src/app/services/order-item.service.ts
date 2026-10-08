import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { OrderItem } from '../models/order-item';

import { getApiBaseUrl } from '../config/api.config';

export type { OrderItem };

@Injectable({
  providedIn: 'root'
})
export class OrderItemService {

  private apiUrl = `${getApiBaseUrl()}/order-items`;
  private cache: OrderItem[] | null = null;

  constructor(private http: HttpClient) { }

  getOrderItems(forceRefresh = false): Observable<OrderItem[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<OrderItem[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getItemsByOrder(orderId: number): Observable<OrderItem[]> {
    return this.http.get<OrderItem[]>(`${this.apiUrl}/order/${orderId}`);
  }

  getOrderItem(id: number): Observable<OrderItem> {
    if (this.cache) {
      const found = this.cache.find(i => i.id === id);
      if (found) return of(found);
    }
    return this.http.get<OrderItem>(`${this.apiUrl}/${id}`);
  }

  createOrderItem(orderItem: Partial<OrderItem>): Observable<OrderItem> {
    return this.http.post<OrderItem>(this.apiUrl, orderItem).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  deleteOrderItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        if (this.cache) {
          this.cache = this.cache.filter(i => i.id !== id);
        }
      })
    );
  }

  clearCache() {
    this.cache = null;
  }
}
