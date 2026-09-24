import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../services/order.service';
import { ToastService } from '../../services/toast.service';
import type { Order, OrderStatus } from '../../models/order';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  private allOrders: Order[] = [];
  searchTerm = '';
  isLoading = true;
  errorMessage = '';

  showModal = false;
  isStatusModal = false;
  currentOrder: Order = this.getEmptyOrder();
  availableStatuses: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  private orderService = inject(OrderService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.allOrders = data;
        this.applySearch();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des commandes', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.orders = this.allOrders.filter((order) =>
      `${order.id} ${order.customerId} ${order.customerName || ''} ${order.status} ${order.totalAmount}`
        .toLowerCase()
        .includes(term),
    );
  }

  getEmptyOrder(): Order {
    return {
      id: 0,
      customerId: 1,
      orderDate: new Date().toISOString(),
      status: 'PENDING',
      totalAmount: 0,
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
      this.orderService
        .updateOrderStatus(this.currentOrder.id, this.currentOrder.status)
        .subscribe({
          next: (updated) => {
            const index = this.orders.findIndex((o) => o.id === updated.id);
            if (index !== -1) {
              this.allOrders = this.allOrders.map((order) =>
                order.id === updated.id ? updated : order,
              );
              this.applySearch();
            }
            this.closeModal();
            this.toastService.show('success', 'Statut modifié', 'Le statut de la commande a été mis à jour.');
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.toastService.show('error', 'Erreur de modification', err?.error?.message || 'Une erreur est survenue.');
            this.cdr.detectChanges();
          }
        });
    } else {
      this.orderService.createOrder(this.currentOrder).subscribe({
        next: (created) => {
          this.allOrders.push(created);
          this.applySearch();
          this.closeModal();
          this.toastService.show('success', 'Commande créée', 'La commande a été ajoutée avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de création', err?.error?.message || 'Une erreur est survenue lors de la création.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteOrder(id: number) {
    this.errorMessage = '';
    if (confirm('Êtes-vous sûr de vouloir supprimer cette commande ?')) {
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          this.allOrders = this.allOrders.filter((o) => o.id !== id);
          this.applySearch();
          this.toastService.show('success', 'Commande supprimée', 'La commande a été supprimée.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de suppression', err?.error?.message || 'Erreur lors de la suppression.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  getStatusBadgeClass(status: OrderStatus): string {
    switch (status) {
      case 'DELIVERED':
        return 'badge-delivered';
      case 'SHIPPED':
        return 'badge-shipped';
      case 'CONFIRMED':
        return 'badge-confirmed';
      case 'CANCELLED':
        return 'badge-cancelled';
      default:
        return 'badge-pending';
    }
  }
}
