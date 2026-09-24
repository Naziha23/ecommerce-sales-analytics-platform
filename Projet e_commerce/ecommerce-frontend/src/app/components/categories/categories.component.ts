import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../services/category.service';
import { ToastService } from '../../services/toast.service';
import type { Category } from '../../models/category';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css'],
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  private allCategories: Category[] = [];
  searchTerm = '';
  isLoading = true;
  errorMessage = '';

  showModal = false;
  isEditMode = false;
  currentCategory: Category = this.getEmptyCategory();

  private categoryService = inject(CategoryService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.allCategories = data;
        this.applySearch();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des catégories', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.categories = this.allCategories.filter((category) =>
      `${category.name} ${category.description || ''}`.toLowerCase().includes(term),
    );
  }

  getEmptyCategory(): Category {
    return { id: 0, name: '', description: '' };
  }

  openAddModal() {
    this.isEditMode = false;
    this.currentCategory = this.getEmptyCategory();
    this.showModal = true;
  }

  openEditModal(category: Category) {
    this.isEditMode = true;
    this.currentCategory = { ...category };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveCategory() {
    if (this.isEditMode) {
      this.categoryService.updateCategory(this.currentCategory.id, this.currentCategory).subscribe({
        next: (updated) => {
          const index = this.categories.findIndex((c) => c.id === updated.id);
          if (index !== -1) {
            this.allCategories = this.allCategories.map((category) =>
              category.id === updated.id ? updated : category,
            );
            this.applySearch();
          }
          this.closeModal();
          this.toastService.show('success', 'Catégorie modifiée', 'La catégorie a été modifiée avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de modification', err?.error?.message || 'Une erreur est survenue lors de la modification.');
          this.cdr.detectChanges();
        }
      });
    } else {
      this.categoryService.createCategory(this.currentCategory).subscribe({
        next: (created) => {
          this.allCategories.push(created);
          this.applySearch();
          this.closeModal();
          this.toastService.show('success', 'Catégorie ajoutée', 'La catégorie a été créée avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de création', err?.error?.message || 'Une erreur est survenue lors de la création.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteCategory(id: number) {
    this.errorMessage = '';
    if (confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.allCategories = this.allCategories.filter((c) => c.id !== id);
          this.applySearch();
          this.toastService.show('success', 'Catégorie supprimée', 'La catégorie a été supprimée.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de suppression', err?.error?.message || 'Erreur lors de la suppression.');
          this.cdr.detectChanges();
        }
      });
    }
  }
}
