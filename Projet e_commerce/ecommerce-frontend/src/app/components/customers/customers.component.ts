import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import { ToastService } from '../../services/toast.service';
import type { Customer } from '../../models/customer';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css'],
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  private allCustomers: Customer[] = [];
  searchTerm = '';
  isLoading = true;
  errorMessage = '';

  showModal = false;
  isEditMode = false;
  currentCustomer: Customer = this.getEmptyCustomer();

  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.customerService.getCustomers().subscribe({
      next: (data) => {
        this.allCustomers = data;
        this.applySearch();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des clients', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.customers = this.allCustomers.filter((customer) =>
      `${customer.firstName} ${customer.lastName} ${customer.email} ${customer.phone || ''} ${customer.city || ''} ${customer.country || ''}`
        .toLowerCase()
        .includes(term),
    );
  }

  getEmptyCustomer(): Customer {
    return {
      id: 0,
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      city: '',
      country: '',
    };
  }

  openAddModal() {
    this.isEditMode = false;
    this.currentCustomer = this.getEmptyCustomer();
    this.showModal = true;
  }

  openEditModal(customer: Customer) {
    this.isEditMode = true;
    this.currentCustomer = { ...customer };
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  saveCustomer() {
    if (this.isEditMode) {
      this.customerService.updateCustomer(this.currentCustomer.id, this.currentCustomer).subscribe({
        next: (updated) => {
          const index = this.customers.findIndex((c) => c.id === updated.id);
          if (index !== -1) {
            this.allCustomers = this.allCustomers.map((customer) =>
              customer.id === updated.id ? updated : customer,
            );
            this.applySearch();
          }
          this.closeModal();
          this.toastService.show('success', 'Client modifié', 'Le client a été modifié avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de modification', err?.error?.message || 'Une erreur est survenue lors de la modification.');
          this.cdr.detectChanges();
        }
      });
    } else {
      this.customerService.createCustomer(this.currentCustomer).subscribe({
        next: (created) => {
          this.allCustomers.push(created);
          this.applySearch();
          this.closeModal();
          this.toastService.show('success', 'Client ajouté', 'Le client a été créé avec succès.');
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.show('error', 'Erreur de création', err?.error?.message || 'Une erreur est survenue lors de la création.');
          this.cdr.detectChanges();
        }
      });
    }
  }

  deleteCustomer(id: number) {
    this.errorMessage = '';
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          this.allCustomers = this.allCustomers.filter((c) => c.id !== id);
          this.applySearch();
          this.toastService.show('success', 'Client supprimé', 'Le client a été supprimé.');
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
