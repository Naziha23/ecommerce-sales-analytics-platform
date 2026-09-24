import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { ProductService } from '../../services/product.service';
import { CustomerService } from '../../services/customer.service';
import { OrderService } from '../../services/order.service';
import { PaymentService } from '../../services/payment.service';
import type { Order } from '../../models/order';
import type { Payment } from '../../models/payment';
import type { Customer } from '../../models/customer';
import type { Product } from '../../models/product';

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

  revenueTrend = 0;
  ordersTrend = 0;
  customersTrend = 0;

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
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.isLoading = true;
    forkJoin({
      products: this.productService.getProducts().pipe(catchError(() => of([]))),
      customers: this.customerService.getCustomers().pipe(catchError(() => of([]))),
      orders: this.orderService.getOrders().pipe(catchError(() => of([]))),
      payments: this.paymentService.getPayments().pipe(catchError(() => of([])))
    }).subscribe({
      next: (data) => {
        this.totalProducts = data.products.length;
        this.totalCustomers = data.customers.length;
        this.totalOrders = data.orders.length;

        this.recentOrders = data.orders.slice(-5).reverse();
        this.buildOrderStatusStats(data.orders);
        
        const paidPayments = data.payments.filter(p => p.status === 'PAID');
        this.totalRevenue = paidPayments.reduce((sum, p) => sum + Number(p.amount), 0);
        this.paidOrdersCount = paidPayments.length;
        this.paymentSuccessRate = data.payments.length > 0 ? Math.round((paidPayments.length / data.payments.length) * 100) : 0;
        this.recentPayments = data.payments.slice(-5).reverse();

        this.calculateTrends(data.orders, paidPayments, data.customers);
        this.computeAdvancedStats();
        this.buildKpis();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des données du dashboard', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  calculateTrends(orders: Order[], paidPayments: Payment[], customers: Customer[]): void {
    const now = new Date();
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    const sixtyDaysAgo = new Date(now.getTime() - 60 * 24 * 60 * 60 * 1000);

    // Revenue Trend
    const recentRevenue = paidPayments
      .filter(p => p.paymentDate && new Date(p.paymentDate) >= thirtyDaysAgo)
      .reduce((sum, p) => sum + Number(p.amount), 0);
    const oldRevenue = paidPayments
      .filter(p => p.paymentDate && new Date(p.paymentDate) >= sixtyDaysAgo && new Date(p.paymentDate) < thirtyDaysAgo)
      .reduce((sum, p) => sum + Number(p.amount), 0);
    
    this.revenueTrend = oldRevenue === 0 ? (recentRevenue > 0 ? 100 : 0) : Math.round(((recentRevenue - oldRevenue) / oldRevenue) * 100);

    // Orders Trend
    const recentOrders = orders.filter(o => o.orderDate && new Date(o.orderDate) >= thirtyDaysAgo).length;
    const oldOrdersCount = orders.filter(o => o.orderDate && new Date(o.orderDate) >= sixtyDaysAgo && new Date(o.orderDate) < thirtyDaysAgo).length;
    this.ordersTrend = oldOrdersCount === 0 ? (recentOrders > 0 ? 100 : 0) : Math.round(((recentOrders - oldOrdersCount) / oldOrdersCount) * 100);

    // Customers Trend
    const recentCustomers = customers.filter(c => c.createdAt && new Date(c.createdAt) >= thirtyDaysAgo).length;
    const oldCustomers = customers.filter(c => c.createdAt && new Date(c.createdAt) >= sixtyDaysAgo && new Date(c.createdAt) < thirtyDaysAgo).length;
    this.customersTrend = oldCustomers === 0 ? (recentCustomers > 0 ? 100 : 0) : Math.round(((recentCustomers - oldCustomers) / oldCustomers) * 100);
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
        trend: this.revenueTrend,
        trendLabel: 'vs mois précédent'
      },
      {
        id: 'orders',
        title: 'Commandes Totales',
        value: this.totalOrders,
        subtitle: 'Volume de ventes',
        type: 'orders',
        theme: 'blue',
        trend: this.ordersTrend,
        trendLabel: 'vs mois précédent'
      },
      {
        id: 'customers',
        title: 'Clients Inscrits',
        value: this.totalCustomers,
        subtitle: 'Comptes actifs',
        type: 'customers',
        theme: 'pink',
        trend: this.customersTrend,
        trendLabel: 'vs mois précédent'
      },
      {
        id: 'products',
        title: 'Catalogue Produits',
        value: this.totalProducts,
        subtitle: 'Articles référencés',
        type: 'products',
        theme: 'orange',
        trend: 0,
        trendLabel: 'catalogue global'
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
