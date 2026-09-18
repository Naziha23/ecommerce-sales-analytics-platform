import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import type { Customer } from '../../models/customer';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  isLoading = true;

  showModal = false;
  isEditMode = false;
  currentCustomer: Customer = this.getEmptyCustomer();

  private customerService = inject(CustomerService);

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.customerService.getCustomers().subscribe({
      next: (data) => {
        this.customers = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des clients', err);
        this.isLoading = false;
      }
    });
  }

  getEmptyCustomer(): Customer {
    return {
      id: 0,
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      city: '',
      country: ''
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
          const index = this.customers.findIndex(c => c.id === updated.id);
          if (index !== -1) {
            this.customers[index] = updated;
          }
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la modification', err)
      });
    } else {
      this.customerService.createCustomer(this.currentCustomer).subscribe({
        next: (created) => {
          this.customers.push(created);
          this.closeModal();
        },
        error: (err) => console.error('Erreur lors de la création', err)
      });
    }
  }

  deleteCustomer(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          this.customers = this.customers.filter(c => c.id !== id);
        },
        error: (err) => console.error('Erreur lors de la suppression', err)
      });
    }
  }
}
