import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Não adicionar token para requisições de login
  if (req.url.includes('/oauth2/token')) {
    console.log('🔓 Requisição de login - sem token:', req.url);
    return next(req);
  }

  const token = authService.getToken();
  
  if (token && authService.isLoggedIn()) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('🔒 Adicionando token para:', req.url);
    return next(authReq);
  }

  console.log('❌ Sem token válido para:', req.url);
  return next(req);
};