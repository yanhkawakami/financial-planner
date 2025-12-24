import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SpendService } from '../../services/spend.service';
import { AuthService } from '../../services/auth.service';
import { Spend, Page } from '../../models/spend.model';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-spend-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './spend-list.component.html',
  styleUrls: ['./spend-list.component.css']
})
export class SpendListComponent implements OnInit {
  spends: Spend[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  loading: boolean = false;
  error: string = '';

  currentUser: User | null = null;

  constructor(
    private spendService: SpendService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.loadSpends();
  }

  loadSpends(): void {
    this.loading = true;
    this.error = '';
    
    const userId = this.authService.getCurrentUserId();
    console.log('👤 Carregando despesas para User ID:', userId);
    
    this.spendService.getSpends(this.currentPage, this.pageSize, userId || undefined).subscribe({
      next: (response: Page<Spend>) => {
        this.spends = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar despesas. Por favor, tente novamente.';
        this.loading = false;
        console.error('Erro ao carregar despesas:', err);
      }
    });
  }

  deleteSpend(id: number | undefined): void {
    if (!id) return;
    
    if (confirm('Tem certeza que deseja excluir esta despesa?')) {
      this.spendService.deleteSpend(id).subscribe({
        next: () => {
          this.loadSpends();
        },
        error: (err) => {
          this.error = 'Erro ao excluir despesa. Por favor, tente novamente.';
          console.error('Erro ao excluir despesa:', err);
        }
      });
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadSpends();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadSpends();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadSpends();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
