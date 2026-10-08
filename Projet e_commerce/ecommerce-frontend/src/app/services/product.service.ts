import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { Product } from '../models/product';

import { getApiBaseUrl } from '../config/api.config';

export type { Product };

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = `${getApiBaseUrl()}/products`;
  private cache: Product[] | null = null;

  constructor(private http: HttpClient) { }

  getProducts(forceRefresh = false): Observable<Product[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<Product[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getProduct(id: number): Observable<Product> {
    if (this.cache) {
      const found = this.cache.find(p => p.id === id);
      if (found) return of(found);
    }
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  searchProducts(keyword: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/search`, {
      params: { keyword }
    });
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product).pipe(
      tap(updated => {
        if (this.cache) {
          const index = this.cache.findIndex(p => p.id === id);
          if (index !== -1) {
            this.cache[index] = updated;
          }
        }
      })
    );
  }

  deleteProduct(id: number): Observable<void> {
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
