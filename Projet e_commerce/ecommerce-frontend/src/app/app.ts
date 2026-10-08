import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth.service';
import { ProductService } from './services/product.service';
import { ToastComponent } from './components/toast/toast.component';

export interface AppNotification {
  title: string;
  message: string;
  level: 'critical' | 'warning';
}

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastComponent],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit, OnDestroy {
  protected readonly title = signal('E-Commerce Admin');
  protected readonly authService = inject(AuthService);
  protected readonly notifications = signal<AppNotification[]>([]);
  protected readonly unreadCount = signal(0);
  protected readonly mobileMenuOpen = signal(false);
  protected showNotifications = false;
  private readonly productService = inject(ProductService);
  private readonly router = inject(Router);
  private intervalId: any;
  private lastSeenCount = 0;

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.refreshNotifications();
    }
    
    // Refresh on navigation and close mobile drawer
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.closeMobileMenu();
      if (this.authService.isAuthenticated()) {
        this.refreshNotifications();
      }
    });

    // Also refresh periodically every 30 seconds
    this.intervalId = setInterval(() => {
      if (this.authService.isAuthenticated()) {
        this.refreshNotifications();
      }
    }, 30000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  refreshNotifications(): void {
    this.productService.getProducts(true).subscribe({
      next: (products) => {
        const newNotifs = products
          .filter((product) => product.stock <= 5)
          .map((product) => ({
            title: product.stock <= 0 ? 'Stock épuisé' : 'Stock faible',
            message:
              product.stock <= 0
                ? `${product.name} est terminé.`
                : `${product.name} : plus que ${product.stock} unité(s).`,
            level: (product.stock <= 0 ? 'critical' : 'warning') as 'critical' | 'warning',
          }));
        
        this.notifications.set(newNotifs);
        
        if (this.showNotifications) {
          this.lastSeenCount = newNotifs.length;
          this.unreadCount.set(0);
        } else {
          const newUnread = Math.max(0, newNotifs.length - this.lastSeenCount);
          this.unreadCount.set(newUnread);
        }
      }
    });
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.lastSeenCount = this.notifications().length;
      this.unreadCount.set(0);
    }
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update(v => !v);
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
