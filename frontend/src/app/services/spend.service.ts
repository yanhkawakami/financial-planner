import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Spend, SpendUpdate, Page } from '../models/spend.model';

@Injectable({
  providedIn: 'root'
})
export class SpendService {
  private apiUrl = 'http://localhost:8080/spends';

  constructor(private http: HttpClient) { }

  getSpends(
    page: number = 0,
    size: number = 10,
    userId?: number,
    startDate?: string,
    finalDate?: string,
    categoryId?: number
  ): Observable<Page<Spend>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (userId) {
      params = params.set('userId', userId.toString());
    }
    if (startDate) {
      params = params.set('startDate', startDate);
    }
    if (finalDate) {
      params = params.set('finalDate', finalDate);
    }
    if (categoryId) {
      params = params.set('categoryId', categoryId.toString());
    }

    console.log('📋 Buscando despesas com parâmetros:', { page, size, userId, startDate, finalDate, categoryId });
    return this.http.get<Page<Spend>>(this.apiUrl, { params });
  }

  getSpendById(id: number): Observable<Spend> {
    console.log('🔍 Buscando despesa por ID:', id);
    console.log('🔍 URL da requisição:', `${this.apiUrl}/${id}`);
    return this.http.get<Spend>(`${this.apiUrl}/${id}`).pipe(
      tap(response => console.log('✅ Resposta do getSpendById:', response)),
      catchError(error => {
        console.error('❌ Erro no getSpendById:', error);
        console.error('❌ Status do erro:', error.status);
        console.error('❌ Mensagem do erro:', error.message);
        throw error;
      })
    );
  }

  createSpend(spend: Spend): Observable<Spend> {
    console.log('➕ Criando nova despesa:', spend);
    return this.http.post<Spend>(this.apiUrl, spend);
  }

  updateSpend(id: number, spend: SpendUpdate): Observable<Spend> {
    console.log('✏️ Atualizando despesa:', id, spend);
    return this.http.put<Spend>(`${this.apiUrl}/${id}`, spend);
  }

  deleteSpend(id: number): Observable<void> {
    console.log('🗑️ Deletando despesa:', id);
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
