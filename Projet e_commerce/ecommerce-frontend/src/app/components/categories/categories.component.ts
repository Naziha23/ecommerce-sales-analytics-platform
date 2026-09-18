import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../services/category.service';
import type { Category } from '../../models/category';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  isLoading = true;

  showModal = false;
  isEditMode = false;
  currentCategory: Category = this.getEmptyCategory();

  private categoryService = inject(CategoryService);

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des catégories', err);
        this.isLoading = false;
      }
    });
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
          const index = this.categories.findIndex(c => c.id === updated.id);
          if (index !== -1) {
            this.categories[index] = updated;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la modification', err)
      });
    } else {
      this.categoryService.createCategory(this.currentCategory).subscribe({
        next: (created) => {
          this.categories.push(created);
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la création', err)
      });
    }
  }

  deleteCategory(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      this.categoryService.deleteCategory(id).subscribe({
        next: () => {
          this.categories = this.categories.filter(c => c.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
      });
    }
  }
}
