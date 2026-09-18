export type PaymentMethod = 'CARD' | 'PAYPAL' | 'CASH_ON_DELIVERY';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';

export interface Payment {
  id: number;
  orderId: number;
  paymentDate?: string;
  method: PaymentMethod;
  status: PaymentStatus;
  amount: number;
}
