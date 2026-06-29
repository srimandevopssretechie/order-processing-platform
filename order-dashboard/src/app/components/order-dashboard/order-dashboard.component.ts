// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { interval, Subscription, switchMap, startWith } from 'rxjs';
import { OrderService } from '../../services/order.service';
import { Order, OrderRequest } from '../../models/order.model';

@Component({
  selector: 'app-order-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './order-dashboard.component.html',
  styleUrl: './order-dashboard.component.css'
})
export class OrderDashboardComponent implements OnInit, OnDestroy {
  orders: Order[] = [];
  message = '';
  isError = false;
  creating = false;

  private refreshSub?: Subscription;

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.startAutoRefresh();
  }

  private startAutoRefresh(): void {
    this.refreshSub = interval(3000)
      .pipe(
        startWith(0),
        switchMap(() => this.orderService.getOrders())
      )
      .subscribe({
        next: (data) => (this.orders = data),
        error: (err) => {
          this.showMessage('Failed to load orders: ' + (err.message || 'Unknown error'), true);
        }
      });
  }

  createSampleOrder(): void {
    const sample: OrderRequest = {
      customerId: `CUST-${Math.floor(Math.random() * 900) + 100}`,
      productId: `PROD-${Math.floor(Math.random() * 900) + 100}`,
      quantity: Math.floor(Math.random() * 10) + 1
    };

    this.creating = true;
    this.orderService.createOrder(sample).subscribe({
      next: (order) => {
        this.showMessage(`Order created! ID: ${order.id}`, false);
        this.creating = false;
      },
      error: (err) => {
        this.showMessage('Failed to create order: ' + (err.error?.message || err.message), true);
        this.creating = false;
      }
    });
  }

  private showMessage(msg: string, isError: boolean): void {
    this.message = msg;
    this.isError = isError;
    setTimeout(() => (this.message = ''), 4000);
  }

  ngOnDestroy(): void {
    this.refreshSub?.unsubscribe();
  }
}
