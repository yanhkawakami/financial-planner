import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserProfile } from '../../models/auth.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  loading: boolean = false;
  error: string = '';
  success: string = '';
  userProfile: UserProfile | null = null;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: [{ value: '', disabled: true }], // Email não editável
      phone: ['', [Validators.required, Validators.pattern(/^\d{10,11}$/)]],
      password: [''],
      confirmPassword: ['']
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    const userId = this.authService.getCurrentUserId();
    
    if (!userId) {
      this.error = 'Usuário não identificado. Por favor, faça login novamente.';
      setTimeout(() => {
        this.authService.logout();
        this.router.navigate(['/login']);
      }, 2000);
      return;
    }

    this.loading = true;
    this.authService.getUserProfile(userId).subscribe({
      next: (profile: UserProfile) => {
        this.userProfile = profile;
        this.profileForm.patchValue({
          name: profile.name,
          email: profile.email,
          phone: profile.phone
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Erro ao carregar perfil:', err);
        this.error = 'Erro ao carregar perfil. Por favor, tente novamente.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      Object.keys(this.profileForm.controls).forEach(key => {
        this.profileForm.get(key)?.markAsTouched();
      });
      return;
    }

    const password = this.profileForm.get('password')?.value;
    const confirmPassword = this.profileForm.get('confirmPassword')?.value;

    // Validar senha se foi fornecida
    if (password || confirmPassword) {
      if (password !== confirmPassword) {
        this.error = 'As senhas não coincidem';
        return;
      }
      if (password.length < 6) {
        this.error = 'A senha deve ter no mínimo 6 caracteres';
        return;
      }
    }

    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.error = 'Usuário não identificado';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const updateData: any = {
      name: this.profileForm.get('name')?.value,
      phone: this.profileForm.get('phone')?.value
    };

    // Só inclui senha se foi fornecida
    if (password) {
      updateData.password = password;
    }

    this.authService.updateUserProfile(userId, updateData).subscribe({
      next: () => {
        this.success = 'Perfil atualizado com sucesso!';
        this.loading = false;
        // Limpar campos de senha
        this.profileForm.patchValue({
          password: '',
          confirmPassword: ''
        });
        // Não redireciona automaticamente - deixa o usuário decidir quando voltar
      },
      error: (err) => {
        this.error = 'Erro ao atualizar perfil. Por favor, tente novamente.';
        console.error('Erro ao atualizar perfil:', err);
        this.loading = false;
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.profileForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getErrorMessage(fieldName: string): string {
    const field = this.profileForm.get(fieldName);
    
    if (field?.hasError('required')) {
      return 'Este campo é obrigatório';
    }
    if (field?.hasError('minlength')) {
      return `Mínimo de ${field.errors?.['minlength'].requiredLength} caracteres`;
    }
    if (field?.hasError('pattern')) {
      if (fieldName === 'phone') {
        return 'Telefone inválido (10-11 dígitos)';
      }
    }
    return '';
  }

  cancel(): void {
    this.router.navigate(['/spends']);
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
