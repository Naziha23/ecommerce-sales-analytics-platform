export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  productName?: string;
  quantity: number;
  unitPrice: number;
}
