import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../services/payment.service';
import type { Payment, PaymentMethod, PaymentStatus } from '../../models/payment';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.css']
})
export class PaymentsComponent implements OnInit {
  payments: Payment[] = [];
  isLoading = true;

  showModal = false;
  currentPayment: Payment = this.getEmptyPayment();

  methods: PaymentMethod[] = ['CARD', 'PAYPAL', 'CASH_ON_DELIVERY'];
  statuses: PaymentStatus[] = ['PENDING', 'PAID', 'FAILED', 'REFUNDED'];

  private paymentService = inject(PaymentService);

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.paymentService.getPayments().subscribe({
      next: (data) => {
        this.payments = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des paiements', err);
        this.isLoading = false;
      }
    });
  }

  getEmptyPayment(): Payment {
    return {
      id: 0,
      orderId: 1,
      paymentDate: new Date().toISOString(),
      method: 'CARD',
      status: 'PAID',
      amount: 0
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
        this.payments.push(created);
        this.closeModal();
      },
      error: (err) => console.error('Erreur lors de l\'enregistrement du paiement', err)
    });
  }

  deletePayment(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce paiement ?')) {
      this.paymentService.deletePayment(id).subscribe({
        next: () => {
          this.payments = this.payments.filter(p => p.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
      });
    }
  }

  getStatusBadgeClass(status: PaymentStatus): string {
    switch (status) {
      case 'PAID': return 'badge-paid';
      case 'PENDING': return 'badge-pending';
      case 'FAILED': return 'badge-failed';
      case 'REFUNDED': return 'badge-refunded';
      default: return 'badge-pending';
    }
  }
}
