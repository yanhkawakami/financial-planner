import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { LoginRequest, AuthResponse, User, TokenPayload } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
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
          this.loadUserFromToken(response.access_token, credentials.username);
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
    if (!token) return false;
    
    try {
      const payload = this.parseJWT(token);
      return payload.exp > Date.now() / 1000;
    } catch (error) {
      return false;
    }
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getCurrentUserId(): number | null {
    const user = this.getCurrentUser();
    const userId = user ? user.id : null;
    
    if (!userId || userId === 0) {
      const token = this.getToken();
      if (token) {
        this.loadUserFromStorage();
        const reloadedUser = this.getCurrentUser();
        return reloadedUser ? reloadedUser.id : null;
      }
    }
    
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
    const token = this.getToken();
    
    if (token && this.isLoggedIn()) {
      
      let storedUsername: string | undefined;
      const storedUserJson = localStorage.getItem(this.userKey);
      if (storedUserJson) {
        try {
          const storedUser = JSON.parse(storedUserJson);
          storedUsername = storedUser.username;
        } catch (e) {
          console.warn('Erro ao ler usuário armazenado', e);
        }
      }

      this.loadUserFromToken(token, storedUsername);
    } else {
      console.log('❌ Nenhum token válido encontrado');
    }
  }

  private loadUserFromToken(token: string, fallbackUsername?: string): void {
    try {
      const payload = this.parseJWT(token);
      const userId = payload.user_id || payload.userId || payload.uid || payload.id || payload.sub;
      
      // Tentar encontrar o nome do usuário em outros campos comuns do JWT
      // Prioriza o nome real (name, given_name) se existir no token
      // Se não, tenta o username/email do login (fallbackUsername)
      // Por fim, tenta encontrar no token (username, user_name, sub)
      const username = payload.name || fallbackUsername || payload.username || 'user';

      const email = payload.username && payload.username.includes('@') ? payload.username : undefined;

      const user: User = {
        id: typeof userId === 'string' ? parseInt(userId) : userId || 1,
        username: username,
        roles: payload.authorities || payload.roles || []
      };
      
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

  recoverToken(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/recover-token`, { email });
  }

  resetPassword(password: string, token: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/auth/new-password`, { password, token });
  }

  register(userData: { name: string; email: string; phone: string; password: string }): Observable<any> {
    const payload = {
      ...userData,
      roles: ['ROLE_USER']
    };
    return this.http.post(`${this.apiUrl}/users`, payload);
  }

  getUserProfile(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/users/${userId}`);
  }

  updateUserProfile(userId: number, userData: { name: string; phone: string; password?: string }): Observable<any> {
    const currentUser = this.getCurrentUser();
    const payload: any = {
      name: userData.name,
      phone: userData.phone,
      roles: currentUser?.roles || ['ROLE_USER']
    };
    
    // Só inclui a senha se foi fornecida
    if (userData.password && userData.password.trim()) {
      payload.password = userData.password;
    }
    
    return this.http.put(`${this.apiUrl}/users/${userId}`, payload).pipe(
      tap(() => {
        // Atualiza o nome do usuário no localStorage
        if (currentUser) {
          currentUser.username = userData.name;
          localStorage.setItem(this.userKey, JSON.stringify(currentUser));
          this.currentUserSubject.next(currentUser);
        }
      })
    );
  }
}