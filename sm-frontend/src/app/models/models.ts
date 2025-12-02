export interface Student {
  id?: number;
  username: string;
  level: 'FRESHMAN' | 'SOPHOMORE' | 'JUNIOR' | 'SENIOR';
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface JwtResponse {
  token: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
