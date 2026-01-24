import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { SpendService } from '../../services/spend.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { Spend, Page } from '../../models/spend.model';
import { Category } from '../../models/category.model';
import { User } from '../../models/auth.model';

@Component({
  selector: 'app-spend-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './spend-list.component.html',
  styleUrls: ['./spend-list.component.css']
})
export class SpendListComponent implements OnInit {
  spends: Spend[] = [];
  categories: Category[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  pageSizeOptions: number[] = [5, 10, 25, 50];
  totalPages: number = 0;
  totalElements: number = 0;
  loading: boolean = false;
  error: string = '';

  // Filtros e ordenação
  selectedCategoryId: number | null = null;
  sortBy: 'date' | 'value' | 'category' = 'date';
  sortDirection: 'asc' | 'desc' = 'desc';

  currentUser: User | null = null;

  constructor(
    private spendService: SpendService,
    private categoryService: CategoryService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    this.loadCategories();
    this.loadSpends();
  }

  loadCategories(): void {
    this.categoryService.getCategories(0, 100).subscribe({
      next: (response) => {
        this.categories = response.content;
      },
      error: (err) => {
        console.error('Erro ao carregar categorias:', err);
      }
    });
  }

  loadSpends(): void {
    this.loading = true;
    this.error = '';
    
    const userId = this.authService.getCurrentUserId();
    
    this.spendService.getSpends(
      this.currentPage, 
      this.pageSize, 
      userId || undefined,
      undefined,
      undefined,
      this.selectedCategoryId || undefined
    ).subscribe({
      next: (response: Page<Spend>) => {
        this.spends = this.sortSpends(response.content);
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

  sortSpends(spends: Spend[]): Spend[] {
    return spends.sort((a, b) => {
      let comparison = 0;

      switch (this.sortBy) {
        case 'date':
          comparison = new Date(a.spendDate).getTime() - new Date(b.spendDate).getTime();
          break;
        case 'value':
          comparison = a.spendValue - b.spendValue;
          break;
        case 'category':
          comparison = a.categoryId - b.categoryId;
          break;
      }

      return this.sortDirection === 'asc' ? comparison : -comparison;
    });
  }

  onSortChange(): void {
    this.spends = this.sortSpends(this.spends);
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadSpends();
  }

  clearFilter(): void {
    this.selectedCategoryId = null;
    this.currentPage = 0;
    this.loadSpends();
  }

  getCategoryName(categoryId: number): string {
    const category = this.categories.find(c => c.id === categoryId);
    return category ? category.name : 'Sem categoria';
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

  onPageSizeChange(): void {
    this.currentPage = 0; // Resetar para primeira página ao mudar o tamanho
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
