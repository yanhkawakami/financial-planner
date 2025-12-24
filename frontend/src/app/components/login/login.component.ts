import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  credentials: LoginRequest = {
    username: '',
    password: ''
  };
  
  loading: boolean = false;
  error: string = '';
  submitted: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Se já estiver logado, redirecionar para a página principal
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/spends']);
    }
  }

  onSubmit(): void {
    this.submitted = true;
    
    if (!this.isFormValid()) {
      return;
    }

    this.loading = true;
    this.error = '';

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/spends']);
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro no login:', err);
        
        if (err.status === 401) {
          this.error = 'Usuário ou senha inválidos.';
        } else if (err.status === 0) {
          this.error = 'Erro de conexão. Verifique se o servidor está funcionando.';
        } else {
          this.error = 'Erro interno do servidor. Tente novamente mais tarde.';
        }
      }
    });
  }

  isFormValid(): boolean {
    return !!(this.credentials.username && this.credentials.password);
  }
}