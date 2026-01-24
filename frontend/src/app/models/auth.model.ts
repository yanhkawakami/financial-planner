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
  username?: string;
  user_name?: string;
  preferred_username?: string;
  name?: string;
  given_name?: string;
  family_name?: string;
  authorities?: string[];
  roles?: string[];
  user_id?: number;
  userId?: number;
  uid?: number | string;
  id?: number;
  [key: string]: any; // Para permitir outros campos
}

export interface UserRegisterRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  roles?: string[];
}

export interface UserProfile {
  id: number;
  name: string;
  email: string;
  phone: string;
  roles: string[];
}

export interface UserUpdateRequest {
  name: string;
  phone: string;
  password?: string;
}

export interface UserRegisterResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
}