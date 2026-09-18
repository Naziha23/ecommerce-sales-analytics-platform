import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CustomerService } from '../../services/customer.service';
import { OrderService } from '../../services/order.service';
import { PaymentService } from '../../services/payment.service';
import type { Order } from '../../models/order';
import type { Payment } from '../../models/payment';

interface KpiCard {
  id: string;
  title: string;
  value: string | number;
  subtitle: string;
  type: 'revenue' | 'orders' | 'customers' | 'products';
  theme: 'pink' | 'green' | 'blue' | 'orange';
  trend: number;
  trendLabel: string;
}

interface OrderStatusStat {
  label: string;
  count: number;
  percent: number;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  isLoading = true;
  kpis: KpiCard[] = [];
  recentOrders: Order[] = [];
  recentPayments: Payment[] = [];
  orderStatusStats: OrderStatusStat[] = [];
  totalRevenue = 0;
  totalOrders = 0;
  totalCustomers = 0;
  totalProducts = 0;
  averageOrderValue = 0;
  paidOrdersCount = 0;
  paymentSuccessRate = 0;

  today = new Date().toLocaleDateString('fr-FR', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });

  private productService = inject(ProductService);
  private customerService = inject(CustomerService);
  private orderService = inject(OrderService);
  private paymentService = inject(PaymentService);

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    let loaded = 0;
    const checkDone = () => {
      loaded++;
      if (loaded === 4) {
        this.computeAdvancedStats();
        this.buildKpis();
        this.isLoading = false;
      }
    };

    this.productService.getProducts().subscribe({
      next: (data) => {
        this.totalProducts = data.length;
        checkDone();
      },
      error: () => checkDone()
    });

    this.customerService.getCustomers().subscribe({
      next: (data) => {
        this.totalCustomers = data.length;
        checkDone();
      },
      error: () => checkDone()
    });

    this.orderService.getOrders().subscribe({
      next: (data) => {
        this.totalOrders = data.length;
        this.recentOrders = data.slice(-5).reverse();
        this.buildOrderStatusStats(data);
        checkDone();
      },
      error: () => checkDone()
    });

    this.paymentService.getPayments().subscribe({
      next: (data) => {
        const paidPayments = data.filter(p => p.status === 'PAID');
        this.totalRevenue = paidPayments.reduce((sum, p) => sum + Number(p.amount), 0);
        this.paidOrdersCount = paidPayments.length;
        this.paymentSuccessRate = data.length > 0 ? Math.round((paidPayments.length / data.length) * 100) : 0;
        this.recentPayments = data.slice(-5).reverse();
        checkDone();
      },
      error: () => checkDone()
    });
  }

  computeAdvancedStats(): void {
    this.averageOrderValue = this.totalOrders > 0 ? Math.round(this.totalRevenue / this.totalOrders) : 0;
  }

  buildKpis(): void {
    this.kpis = [
      {
        id: 'revenue',
        title: 'Chiffre d\'Affaires',
        value: this.formatCurrency(this.totalRevenue),
        subtitle: 'Paiements encaissés',
        type: 'revenue',
        theme: 'green',
        trend: 12.5,
        trendLabel: 'vs mois précédent'
      },
      {
        id: 'orders',
        title: 'Commandes Totales',
        value: this.totalOrders,
        subtitle: 'Volume de ventes',
        type: 'orders',
        theme: 'blue',
        trend: 8.2,
        trendLabel: 'croissance continue'
      },
      {
        id: 'customers',
        title: 'Clients Inscrits',
        value: this.totalCustomers,
        subtitle: 'Comptes actifs',
        type: 'customers',
        theme: 'pink',
        trend: 4.8,
        trendLabel: 'nouveaux clients'
      },
      {
        id: 'products',
        title: 'Catalogue Produits',
        value: this.totalProducts,
        subtitle: 'Articles référencés',
        type: 'products',
        theme: 'orange',
        trend: 0,
        trendLabel: 'catalogue à jour'
      }
    ];
  }

  buildOrderStatusStats(orders: Order[]): void {
    const statusMap: Record<string, number> = {};
    orders.forEach(o => {
      statusMap[o.status] = (statusMap[o.status] || 0) + 1;
    });

    const total = orders.length || 1;
    const statusConfig: Record<string, { label: string; color: string }> = {
      DELIVERED: { label: 'Livrées', color: '#10b981' },
      SHIPPED:   { label: 'Expédiées', color: '#3b82f6' },
      CONFIRMED: { label: 'Confirmées', color: '#6366f1' },
      PENDING:   { label: 'En attente', color: '#f59e0b' },
      CANCELLED: { label: 'Annulées', color: '#ef4444' }
    };

    this.orderStatusStats = Object.keys(statusConfig).map(status => ({
      label: statusConfig[status].label,
      count: statusMap[status] || 0,
      percent: Math.round(((statusMap[status] || 0) / total) * 100),
      color: statusConfig[status].color
    })).filter(s => s.count > 0);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
      maximumFractionDigits: 0
    }).format(value);
  }

  getStatusBadge(status: string): string {
    const map: Record<string, string> = {
      DELIVERED: 'badge-delivered',
      SHIPPED:   'badge-shipped',
      CONFIRMED: 'badge-confirmed',
      PENDING:   'badge-pending',
      CANCELLED: 'badge-cancelled',
      PAID:      'badge-paid',
      FAILED:    'badge-failed',
      REFUNDED:  'badge-refunded'
    };
    return map[status] || 'badge-default';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      DELIVERED: 'Livrée',
      SHIPPED:   'Expédiée',
      CONFIRMED: 'Confirmée',
      PENDING:   'En attente',
      CANCELLED: 'Annulée',
      PAID:      'Payé',
      FAILED:    'Échoué',
      REFUNDED:  'Remboursé'
    };
    return map[status] || status;
  }
}
