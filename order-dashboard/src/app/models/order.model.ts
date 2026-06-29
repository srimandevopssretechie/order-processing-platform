// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
export type OrderStatus = 'CREATED' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED';

export interface OrderRequest {
  customerId: string;
  productId: string;
  quantity: number;
}

export interface Order {
  id: string;
  customerId: string;
  productId: string;
  quantity: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt: string;
}
