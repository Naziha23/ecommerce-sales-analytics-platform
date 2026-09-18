import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
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
  
  showModal = false;
  isEditMode = false;
  currentProduct: Product = this.getEmptyProduct();

  private productService = inject(ProductService);

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des produits', err);
        this.isLoading = false;
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
        },
        error: (err) => console.error('Erreur lors de la modification', err)
      });
    } else {
      // Pour la création, on peut retirer l'id s'il est auto-généré, ou backend gère l'id=0
      this.productService.createProduct(this.currentProduct).subscribe({
        next: (created) => {
          this.products.push(created);
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la création', err)
      });
    }
  }

  deleteProduct(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.products = this.products.filter(p => p.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
      });
    }
  }
}
