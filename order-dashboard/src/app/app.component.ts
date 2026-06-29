// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
import { Component } from '@angular/core';
import { OrderDashboardComponent } from './components/order-dashboard/order-dashboard.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [OrderDashboardComponent],
  template: `<app-order-dashboard></app-order-dashboard>`,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'order-dashboard';
}
