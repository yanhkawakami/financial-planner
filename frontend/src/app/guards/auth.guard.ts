import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  console.log('🔐 Verificando autenticação...');
  const isLoggedIn = authService.isLoggedIn();
  console.log('🔐 Usuário logado:', isLoggedIn);

  if (isLoggedIn) {
    console.log('✅ Usuário autenticado - permitindo acesso');
    return true;
  } else {
    console.log('❌ Usuário não autenticado - redirecionando para login');
    router.navigate(['/login']);
    return false;
  }
};