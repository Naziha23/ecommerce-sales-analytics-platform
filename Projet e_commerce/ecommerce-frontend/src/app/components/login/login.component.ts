import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;
  showPassword = false;

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  submit(): void {
    if (!this.username.trim() || !this.password) {
      this.errorMessage = 'Veuillez saisir votre identifiant et votre mot de passe.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.authService.login({ username: this.username.trim(), password: this.password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.errorMessage = 'Identifiant ou mot de passe incorrect.';
        this.isLoading = false;
      }
    });
  }
}
