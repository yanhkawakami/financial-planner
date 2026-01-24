import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { SpendService } from '../../services/spend.service';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { Spend, Page } from '../../models/spend.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-monthly-summary',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './monthly-summary.component.html',
  styleUrls: ['./monthly-summary.component.css']
})
export class MonthlySummaryComponent implements OnInit {
  spends: Spend[] = [];
  categories: Map<number, string> = new Map();
  months = [
    { value: 0, label: 'Janeiro' },
    { value: 1, label: 'Fevereiro' },
    { value: 2, label: 'Março' },
    { value: 3, label: 'Abril' },
    { value: 4, label: 'Maio' },
    { value: 5, label: 'Junho' },
    { value: 6, label: 'Julho' },
    { value: 7, label: 'Agosto' },
    { value: 8, label: 'Setembro' },
    { value: 9, label: 'Outubro' },
    { value: 10, label: 'Novembro' },
    { value: 11, label: 'Dezembro' }
  ];
  
  years: number[] = [];
  selectedYear: number = new Date().getFullYear();
  
  filterForm: FormGroup;
  
  // Pagination Data
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  
  // Summary Data
  loading: boolean = false;
  summaryLoading: boolean = false;
  error: string = '';
  totalValue: number = 0;
  topCategoryId: number | null = null;
  topCategoryName: string = '';

  constructor(
    private spendService: SpendService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private fb: FormBuilder
  ) {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    const lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    this.filterForm = this.fb.group({
      startDate: [this.formatDate(firstDay)],
      endDate: [this.formatDate(lastDay)]
    });
    
    // Generate last 5 years (leaving this if we need it later, but unused for now)
    const currentYear = new Date().getFullYear();
    for (let i = 0; i < 5; i++) {
      this.years.push(currentYear - i);
    }
  }

  ngOnInit(): void {
    this.loadCategories();
    this.applyFilter();
  }

  loadCategories(): void {
    this.categoryService.getCategories(0, 100).subscribe({
      next: (page) => {
        page.content.forEach(cat => {
          if (cat.id) this.categories.set(cat.id, cat.name);
        });
      }
    });
  }

  applyFilter(): void {
    this.currentPage = 0;
    this.loadSpends();
    this.calculateSummary();
  }

  loadSpends(): void {
    this.loading = true;
    this.error = '';
    
    const userId = this.authService.getCurrentUserId();
    const startDate = this.filterForm.get('startDate')?.value;
    const endDate = this.filterForm.get('endDate')?.value;

    this.spendService.getSpends(
      this.currentPage, 
      this.pageSize, 
      userId || undefined, 
      startDate, 
      endDate
    ).subscribe({
      next: (response: Page<Spend>) => {
        this.spends = response.content.sort((a, b) => a.categoryId - b.categoryId);
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar despesas', err);
        this.error = 'Erro ao carregar despesas. Tente novamente.';
        this.loading = false;
      }
    });
  }

  calculateSummary(): void {
    this.summaryLoading = true;
    const userId = this.authService.getCurrentUserId();
    const startDate = this.filterForm.get('startDate')?.value;
    const endDate = this.filterForm.get('endDate')?.value;

    // Fetch all records for the period to calculate totals (using a large page size)
    this.spendService.getSpends(
      0, 
      10000, 
      userId || undefined, 
      startDate, 
      endDate
    ).subscribe({
      next: (response: Page<Spend>) => {
        const allSpends = response.content;
        
        // Calculate Total
        this.totalValue = allSpends.reduce((acc, curr) => acc + curr.spendValue, 0);

        // Calculate Top Category
        const categoryTotals = new Map<number, number>();
        allSpends.forEach(spend => {
          const currentTotal = categoryTotals.get(spend.categoryId) || 0;
          categoryTotals.set(spend.categoryId, currentTotal + spend.spendValue);
        });

        let maxTotal = 0;
        this.topCategoryId = null;

        categoryTotals.forEach((total, categoryId) => {
          if (total > maxTotal) {
            maxTotal = total;
            this.topCategoryId = categoryId;
          }
        });

        if (this.topCategoryId) {
           // We might need to wait for categories to load if they haven't yet, 
           // but normally they load fast.
           this.topCategoryName = this.categories.get(this.topCategoryId) || 'Carregando...';
        } else {
           this.topCategoryName = '-';
        }

        this.summaryLoading = false;
      },
      error: (err) => {
        console.error('Erro ao calcular resumo', err);
        this.summaryLoading = false;
      }
    });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadSpends();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadSpends();
    }
  }

  // Helper to format date as YYYY-MM-DD
  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}
