import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  password: string = '';
  confirmPassword: string = '';
  token: string = '';
  loading: boolean = false;
  error: string = '';
  success: boolean = false;
  submitted: boolean = false;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Capturar o token da URL (path parameter)
    this.route.params.subscribe(params => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.error = 'Token inválido ou ausente.';
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    
    if (!this.isFormValid()) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = false;

    this.authService.resetPassword(this.password, this.token).subscribe({
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
        console.error('Erro ao redefinir senha:', err);
        
        if (err.status === 400) {
          this.error = 'Token inválido ou expirado.';
        } else if (err.status === 0) {
          this.error = 'Erro de conexão. Verifique se o servidor está funcionando.';
        } else {
          this.error = 'Erro ao redefinir senha. Tente novamente mais tarde.';
        }
      }
    });
  }

  isFormValid(): boolean {
    return !!(
      this.token && 
      this.password && 
      this.confirmPassword && 
      this.password === this.confirmPassword &&
      this.password.length >= 6
    );
  }

  passwordsMatch(): boolean {
    return this.password === this.confirmPassword;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
