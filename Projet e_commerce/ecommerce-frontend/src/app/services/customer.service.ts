import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { Customer } from '../models/customer';

import { getApiBaseUrl } from '../config/api.config';

export type { Customer };

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private apiUrl = `${getApiBaseUrl()}/customers`;
  private cache: Customer[] | null = null;

  constructor(private http: HttpClient) { }

  getCustomers(forceRefresh = false): Observable<Customer[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<Customer[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getCustomer(id: number): Observable<Customer> {
    if (this.cache) {
      const found = this.cache.find(c => c.id === id);
      if (found) return of(found);
    }
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  createCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.apiUrl, customer).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  updateCustomer(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.apiUrl}/${id}`, customer).pipe(
      tap(updated => {
        if (this.cache) {
          const index = this.cache.findIndex(c => c.id === id);
          if (index !== -1) {
            this.cache[index] = updated;
          }
        }
      })
    );
  }

  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        if (this.cache) {
          this.cache = this.cache.filter(c => c.id !== id);
        }
      })
    );
  }

  clearCache() {
    this.cache = null;
  }
}
