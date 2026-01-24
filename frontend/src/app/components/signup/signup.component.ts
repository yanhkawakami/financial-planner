import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

export interface UserRegister {
  name: string;
  email: string;
  phone: string;
  password: string;
}

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  user: UserRegister = {
    name: '',
    email: '',
    phone: '',
    password: ''
  };
  
  confirmPassword: string = '';
  loading: boolean = false;
  error: string = '';
  success: boolean = false;
  submitted: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

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

    this.authService.register(this.user).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        
        // Redirecionar para o login após 2 segundos
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro ao criar usuário:', err);
        
        if (err.status === 400) {
          this.error = 'Dados inválidos. Verifique as informações fornecidas.';
        } else if (err.status === 409) {
          this.error = 'E-mail já cadastrado.';
        } else if (err.status === 0) {
          this.error = 'Erro de conexão. Verifique se o servidor está funcionando.';
        } else {
          this.error = 'Erro ao criar conta. Tente novamente mais tarde.';
        }
      }
    });
  }

  isFormValid(): boolean {
    return !!(
      this.user.name && 
      this.user.email && 
      this.isValidEmail(this.user.email) &&
      this.user.phone &&
      this.isValidPhone(this.user.phone) &&
      this.user.password && 
      this.confirmPassword && 
      this.user.password === this.confirmPassword &&
      this.user.password.length >= 6
    );
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  isValidPhone(phone: string): boolean {
    // Remove tudo que não é número
    const cleanPhone = phone.replace(/\D/g, '');
    // Aceita de 10 a 11 dígitos (com ou sem DDD)
    return cleanPhone.length >= 10 && cleanPhone.length <= 11;
  }

  passwordsMatch(): boolean {
    return this.user.password === this.confirmPassword;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
