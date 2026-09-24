import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../services/payment.service';
import { ToastService } from '../../services/toast.service';
import type { Payment, PaymentMethod, PaymentStatus } from '../../models/payment';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.css'],
})
export class PaymentsComponent implements OnInit {
  payments: Payment[] = [];
  private allPayments: Payment[] = [];
  searchTerm = '';
  isLoading = true;

  showModal = false;
  currentPayment: Payment = this.getEmptyPayment();

  methods: PaymentMethod[] = ['CARD', 'PAYPAL', 'CASH_ON_DELIVERY'];
  statuses: PaymentStatus[] = ['PENDING', 'PAID', 'FAILED', 'REFUNDED'];

  private paymentService = inject(PaymentService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.isLoading = true;
    this.paymentService.getPayments().subscribe({
      next: (data) => {
        this.allPayments = data;
        this.applySearch();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des paiements', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.payments = this.allPayments.filter((payment) =>
      `${payment.id} ${payment.orderId} ${payment.method} ${payment.status} ${payment.amount}`
        .toLowerCase()
        .includes(term),
    );
  }

  getEmptyPayment(): Payment {
    return {
      id: 0,
      orderId: 1,
      paymentDate: new Date().toISOString(),
      method: 'CARD',
      status: 'PAID',
      amount: 0,
    };
  }

  openAddModal() {
    this.currentPayment = this.getEmptyPayment();
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  savePayment() {
    this.paymentService.createPayment(this.currentPayment).subscribe({
      next: (created) => {
        this.allPayments.push(created);
        this.applySearch();
        this.closeModal();
        this.toastService.show('success', 'Paiement enregistré', 'Le paiement a été ajouté avec succès.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toastService.show('error', 'Erreur d\'enregistrement', err?.error?.message || 'Une erreur est survenue.');
        this.cdr.detectChanges();
      }
    });
  }

  deletePayment(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce paiement ?')) {
      this.paymentService.deletePayment(id).subscribe({
        next: () => {
          this.allPayments = this.allPayments.filter((p) => p.id !== id);
          this.applySearch();
          this.toastService.show('success', 'Paiement supprimé', 'Le paiement a été supprimé.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de suppression', err?.error?.message || 'Erreur lors de la suppression.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  getStatusBadgeClass(status: PaymentStatus): string {
    switch (status) {
      case 'PAID':
        return 'badge-paid';
      case 'PENDING':
        return 'badge-pending';
      case 'FAILED':
        return 'badge-failed';
      case 'REFUNDED':
        return 'badge-refunded';
      default:
        return 'badge-pending';
    }
  }
}
