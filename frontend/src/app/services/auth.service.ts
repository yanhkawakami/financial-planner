import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { LoginRequest, AuthResponse, User, TokenPayload } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';
  private tokenKey = 'financial_planner_token';
  private userKey = 'financial_planner_user';
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    console.log('🔧 Inicializando AuthService...');
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Basic ' + btoa('myclientid:myclientsecret') // Ajuste conforme sua configuração
    });

    const body = new URLSearchParams();
    body.set('username', credentials.username);
    body.set('password', credentials.password);
    body.set('grant_type', 'password');

    return this.http.post<AuthResponse>(`${this.apiUrl}/oauth2/token`, body.toString(), { headers })
      .pipe(
        tap((response: AuthResponse) => {
          console.log('🔐 Token recebido do servidor:', response);
          this.setTokens(response);
          this.loadUserFromToken(response.access_token);
        }),
        catchError((error) => {
          console.error('Erro no login:', error);
          throw error;
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    console.log('🔍 Verificando se está logado...');
    console.log('🔍 Token encontrado:', token ? 'SIM' : 'NÃO');
    
    if (!token) {
      console.log('❌ Nenhum token encontrado');
      return false;
    }
    
    try {
      const payload = this.parseJWT(token);
      const isValid = payload.exp > Date.now() / 1000;
      console.log('🔍 Token válido:', isValid);
      console.log('🔍 Token expira em:', new Date(payload.exp * 1000));
      console.log('🔍 Data atual:', new Date());
      return isValid;
    } catch (error) {
      console.log('❌ Erro ao validar token:', error);
      return false;
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getCurrentUserId(): number | null {
    const user = this.getCurrentUser();
    const userId = user ? user.id : null;
    console.log('🔍 getCurrentUserId() retornando:', userId);
    return userId;
  }

  hasValidUserId(): boolean {
    const userId = this.getCurrentUserId();
    return userId !== null && userId !== undefined && userId > 0;
  }

  private setTokens(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.access_token);
  }

  private loadUserFromStorage(): void {
    console.log('🔍 Carregando usuário do localStorage...');
    const token = this.getToken();
    console.log('🔍 Token do localStorage:', token ? 'EXISTE' : 'NÃO EXISTE');
    
    if (token && this.isLoggedIn()) {
      console.log('✅ Token válido encontrado - carregando usuário');
      this.loadUserFromToken(token);
    } else {
      console.log('❌ Nenhum token válido encontrado');
    }
  }

  private loadUserFromToken(token: string): void {
    try {
      const payload = this.parseJWT(token);
      console.log('🔍 Payload completo do JWT:', payload);
      
      // Tentar diferentes campos possíveis para user ID
      const userId = payload.user_id || payload.userId || payload.uid || payload.id || payload.sub;
      console.log('🆔 User ID extraído:', userId);
      
      const user: User = {
        id: typeof userId === 'string' ? parseInt(userId) : userId || 1,
        username: payload.sub,
        roles: payload.authorities || payload.roles || []
      };
      
      console.log('👤 Usuário criado:', user);
      localStorage.setItem(this.userKey, JSON.stringify(user));
      this.currentUserSubject.next(user);
    } catch (error) {
      console.error('Erro ao carregar usuário do token:', error);
      this.logout();
    }
  }

  private parseJWT(token: string): TokenPayload {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      throw new Error('Token inválido');
    }
  }
}