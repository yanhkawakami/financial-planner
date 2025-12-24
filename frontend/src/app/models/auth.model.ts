export interface LoginRequest {
  username: string;
  password: string;
  grant_type?: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  refresh_token?: string;
  scope?: string;
}

export interface User {
  id: number;
  username: string;
  email?: string;
  roles: string[];
}

export interface TokenPayload {
  sub: string;
  exp: number;
  iat: number;
  authorities?: string[];
  roles?: string[];
  user_id?: number;
  userId?: number;
  uid?: number | string;
  id?: number;
  [key: string]: any; // Para permitir outros campos
}