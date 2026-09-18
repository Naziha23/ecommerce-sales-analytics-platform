import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderItemService } from '../../services/order-item.service';
import type { OrderItem } from '../../models/order-item';

@Component({
  selector: 'app-order-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-items.component.html',
  styleUrls: ['./order-items.component.css']
})
export class OrderItemsComponent implements OnInit {
  orderItems: OrderItem[] = [];
  isLoading = true;

  showModal = false;
  currentItem: OrderItem = this.getEmptyItem();
  filterOrderId: number | null = null;

  private orderItemService = inject(OrderItemService);

  ngOnInit(): void {
    this.loadOrderItems();
  }

  loadOrderItems(): void {
    this.isLoading = true;
    if (this.filterOrderId) {
      this.orderItemService.getItemsByOrder(this.filterOrderId).subscribe({
        next: (data) => {
          this.orderItems = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des articles de commande', err);
          this.isLoading = false;
        }
      });
    } else {
      this.orderItemService.getOrderItems().subscribe({
        next: (data) => {
          this.orderItems = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des articles de commande', err);
          this.isLoading = false;
        }
      });
    }
  }

  getEmptyItem(): OrderItem {
    return {
      id: 0,
      orderId: 1,
      productId: 1,
      quantity: 1,
      unitPrice: 0
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
        this.orderItems.push(created);
        this.closeModal();
      },
      error: (err) => console.error('Erreur lors de l\'ajout de l\'article', err)
    });
  }

  deleteOrderItem(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette ligne de commande ?')) {
      this.orderItemService.deleteOrderItem(id).subscribe({
        next: () => {
          this.orderItems = this.orderItems.filter(item => item.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
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
