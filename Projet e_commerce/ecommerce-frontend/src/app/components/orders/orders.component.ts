import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../services/order.service';
import type { Order, OrderStatus } from '../../models/order';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  isLoading = true;

  showModal = false;
  isStatusModal = false;
  currentOrder: Order = this.getEmptyOrder();
  availableStatuses: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  private orderService = inject(OrderService);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des commandes', err);
        this.isLoading = false;
      }
    });
  }

  getEmptyOrder(): Order {
    return {
      id: 0,
      customerId: 1,
      orderDate: new Date().toISOString(),
      status: 'PENDING',
      totalAmount: 0
    };
  }

  openAddModal() {
    this.isStatusModal = false;
    this.currentOrder = this.getEmptyOrder();
    this.showModal = true;
  }

  openStatusModal(order: Order) {
    this.isStatusModal = true;
    this.currentOrder = { ...order };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveOrder() {
    if (this.isStatusModal) {
      this.orderService.updateOrderStatus(this.currentOrder.id, this.currentOrder.status).subscribe({
        next: (updated) => {
          const index = this.orders.findIndex(o => o.id === updated.id);
          if (index !== -1) {
            this.orders[index] = updated;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors du changement de statut', err)
      });
    } else {
      this.orderService.createOrder(this.currentOrder).subscribe({
        next: (created) => {
          this.orders.push(created);
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la création', err)
      });
    }
  }

  deleteOrder(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette commande ?')) {
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          this.orders = this.orders.filter(o => o.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
      });
    }
  }

  getStatusBadgeClass(status: OrderStatus): string {
    switch (status) {
      case 'DELIVERED': return 'badge-delivered';
      case 'SHIPPED': return 'badge-shipped';
      case 'CONFIRMED': return 'badge-confirmed';
      case 'CANCELLED': return 'badge-cancelled';
      default: return 'badge-pending';
    }
  }
}
