import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { ToastService } from '../../services/toast.service';
import type { Product } from '../../models/product';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  isLoading = true;
  searchTerm = '';
  errorMessage = '';
  
  showModal = false;
  isEditMode = false;
  currentProduct: Product = this.getEmptyProduct();

  private productService = inject(ProductService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des produits', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  searchProducts(): void {
    const keyword = this.searchTerm.trim();
    if (!keyword) {
      this.loadProducts();
      return;
    }

    this.isLoading = true;
    this.productService.searchProducts(keyword).subscribe({
      next: (data) => {
        this.products = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors de la recherche des produits', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getEmptyProduct(): Product {
    return { id: 0, name: '', description: '', price: 0, stock: 0, categoryId: 1 };
  }

  openAddModal() {
    this.isEditMode = false;
    this.currentProduct = this.getEmptyProduct();
    this.showModal = true;
  }

  openEditModal(product: Product) {
    this.isEditMode = true;
    this.currentProduct = { ...product };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveProduct() {
    if (this.isEditMode) {
      this.productService.updateProduct(this.currentProduct.id, this.currentProduct).subscribe({
        next: (updated) => {
          const index = this.products.findIndex(p => p.id === updated.id);
          if (index !== -1) {
            this.products[index] = updated;
          }
          this.closeModal();
          this.toastService.show('success', 'Produit modifié', 'Le produit a été modifié avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de modification', err?.error?.message || 'Une erreur est survenue lors de la modification.');
          this.cdr.detectChanges();
        }
      });
    } else {
      // Pour la création, on peut retirer l'id s'il est auto-généré, ou backend gère l'id=0
      this.productService.createProduct(this.currentProduct).subscribe({
        next: (created) => {
          this.products.push(created);
          this.closeModal();
          this.toastService.show('success', 'Produit ajouté', 'Le produit a été créé avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de création', err?.error?.message || 'Une erreur est survenue lors de la création.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteProduct(id: number) {
    this.errorMessage = '';
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== id);
          this.toastService.show('success', 'Produit supprimé', 'Le produit a été supprimé.');
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
