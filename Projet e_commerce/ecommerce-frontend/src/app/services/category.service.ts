import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { Category } from '../models/category';

export type { Category };

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = 'http://127.0.0.1:8081/api/categories';
  private cache: Category[] | null = null;

  constructor(private http: HttpClient) { }

  getCategories(forceRefresh = false): Observable<Category[]> {
    if (this.cache && !forceRefresh) {
      return of(this.cache);
    }
    return this.http.get<Category[]>(this.apiUrl).pipe(
      tap(data => this.cache = data)
    );
  }

  getCategory(id: number): Observable<Category> {
    if (this.cache) {
      const found = this.cache.find(c => c.id === id);
      if (found) return of(found);
    }
    return this.http.get<Category>(`${this.apiUrl}/${id}`);
  }

  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category).pipe(
      tap(created => {
        if (this.cache) {
          this.cache.push(created);
        }
      })
    );
  }

  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category).pipe(
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

  deleteCategory(id: number): Observable<void> {
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
