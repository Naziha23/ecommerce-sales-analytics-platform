import type { OrderItem } from './order-item';

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface Order {
  id: number;
  customerId: number;
  customerName?: string;
  orderDate?: string;
  status: OrderStatus;
  totalAmount: number;
  orderItems?: OrderItem[];
}
