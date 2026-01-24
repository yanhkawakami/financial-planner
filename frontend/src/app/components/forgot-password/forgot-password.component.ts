import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  email: string = '';
  loading: boolean = false;
  error: string = '';
  success: boolean = false;
  submitted: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit(): void {
    this.submitted = true;
    
    if (!this.isFormValid()) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = false;

    this.authService.recoverToken(this.email).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.email = '';
        this.submitted = false;
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro ao solicitar recuperação:', err);
        
        if (err.status === 404) {
          this.error = 'E-mail não encontrado.';
        } else if (err.status === 0) {
          this.error = 'Erro de conexão. Verifique se o servidor está funcionando.';
        } else {
          this.error = 'Erro ao solicitar recuperação. Tente novamente mais tarde.';
        }
      }
    });
  }

  isFormValid(): boolean {
    return !!(this.email && this.isValidEmail(this.email));
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}
