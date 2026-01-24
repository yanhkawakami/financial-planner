import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Não adicionar token para requisições públicas
  if (req.url.includes('/oauth2/token') || 
      req.url.includes('/users') && req.method === 'POST' ||
      req.url.includes('/auth/recover-token') ||
      req.url.includes('/auth/new-password')) {
    return next(req);
  }

  const token = authService.getToken();
  
  if (token && authService.isLoggedIn()) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }

  return next(req);
};
