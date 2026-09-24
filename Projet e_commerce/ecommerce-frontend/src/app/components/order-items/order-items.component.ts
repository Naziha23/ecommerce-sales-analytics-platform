import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderItemService } from '../../services/order-item.service';
import { ToastService } from '../../services/toast.service';
import type { OrderItem } from '../../models/order-item';

@Component({
  selector: 'app-order-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-items.component.html',
  styleUrls: ['./order-items.component.css'],
})
export class OrderItemsComponent implements OnInit {
  orderItems: OrderItem[] = [];
  private allOrderItems: OrderItem[] = [];
  isLoading = true;

  showModal = false;
  currentItem: OrderItem = this.getEmptyItem();
  filterOrderId: number | null = null;
  searchTerm = '';

  private orderItemService = inject(OrderItemService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadOrderItems();
  }

  loadOrderItems(): void {
    this.isLoading = true;
    if (this.filterOrderId) {
      this.orderItemService.getItemsByOrder(this.filterOrderId).subscribe({
        next: (data) => {
          this.allOrderItems = data;
          this.applySearch();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erreur lors du chargement des articles de commande', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    } else {
      this.orderItemService.getOrderItems().subscribe({
        next: (data) => {
          this.allOrderItems = data;
          this.applySearch();
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Erreur lors du chargement des articles de commande', err);
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    }
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.orderItems = this.allOrderItems.filter((item) =>
      `${item.id} ${item.orderId} ${item.productId} ${item.productName || ''} ${item.quantity} ${item.unitPrice}`
        .toLowerCase()
        .includes(term),
    );
  }

  getEmptyItem(): OrderItem {
    return {
      id: 0,
      orderId: 1,
      productId: 1,
      quantity: 1,
      unitPrice: 0,
    };
  }

  openAddModal() {
    this.currentItem = this.getEmptyItem();
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveOrderItem() {
    this.orderItemService.createOrderItem(this.currentItem).subscribe({
      next: (created) => {
        this.allOrderItems.push(created);
        this.applySearch();
        this.closeModal();
        this.toastService.show('success', 'Ligne ajoutée', 'La ligne de commande a été ajoutée avec succès.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toastService.show('error', 'Erreur d\'ajout', err?.error?.message || 'Une erreur est survenue.');
        this.cdr.detectChanges();
      }
    });
  }

  deleteOrderItem(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette ligne de commande ?')) {
      this.orderItemService.deleteOrderItem(id).subscribe({
        next: () => {
          this.allOrderItems = this.allOrderItems.filter((item) => item.id !== id);
          this.applySearch();
          this.toastService.show('success', 'Ligne supprimée', 'La ligne de commande a été supprimée.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de suppression', err?.error?.message || 'Erreur lors de la suppression.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  onFilterChange() {
    this.loadOrderItems();
  }

  clearFilter() {
    this.filterOrderId = null;
    this.loadOrderItems();
  }
}
