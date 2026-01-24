import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SpendService } from '../../services/spend.service';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { Spend, Page } from '../../models/spend.model';
import { Category } from '../../models/category.model';

interface MonthData {
  month: string;
  year: number;
  categoryTotals: Map<string, number>;
  total: number;
  maxCategory: string;
  maxValue: number;
}

@Component({
  selector: 'app-quarterly-comparison',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quarterly-comparison.component.html',
  styleUrls: ['./quarterly-comparison.component.css']
})
export class QuarterlyComparisonComponent implements OnInit {
  monthsData: MonthData[] = [];
  allCategories: string[] = [];
  loading: boolean = false;
  error: string = '';

  monthNames = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

  constructor(
    private spendService: SpendService,
    private authService: AuthService,
    private categoryService: CategoryService
  ) { }

  ngOnInit(): void {
    this.loadQuarterlyData();
  }

  loadQuarterlyData(): void {
    this.loading = true;
    this.error = '';

    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      this.error = 'Usuário não autenticado';
      this.loading = false;
      return;
    }

    // Calcular os últimos 3 meses
    const today = new Date();
    const months: Date[] = [];
    
    for (let i = 2; i >= 0; i--) {
      const date = new Date(today.getFullYear(), today.getMonth() - i, 1);
      months.push(date);
    }

    // Carregar categorias primeiro
    this.categoryService.getCategories(0, 100).subscribe({
      next: (categoryPage: Page<Category>) => {
        const categoryMap = new Map<number, string>();
        categoryPage.content.forEach(cat => {
          categoryMap.set(cat.id, cat.name);
        });

        // Carregar dados de cada mês
        const requests = months.map(date => {
          const startDate = this.formatDate(new Date(date.getFullYear(), date.getMonth(), 1));
          const endDate = this.formatDate(new Date(date.getFullYear(), date.getMonth() + 1, 0));
          
          return this.spendService.getSpends(0, 1000, userId, startDate, endDate);
        });

        // Processar todos os meses em paralelo
        Promise.all(requests.map(req => req.toPromise())).then(results => {
          this.monthsData = results.map((result, index) => {
            const date = months[index];
            const spends = result?.content || [];
            
            const categoryTotals = new Map<string, number>();
            let total = 0;
            
            spends.forEach(spend => {
              const categoryName = categoryMap.get(spend.categoryId) || 'Sem categoria';
              const currentTotal = categoryTotals.get(categoryName) || 0;
              categoryTotals.set(categoryName, currentTotal + spend.spendValue);
              total += spend.spendValue;
            });

            // Encontrar categoria com maior gasto
            let maxCategory = '';
            let maxValue = 0;
            categoryTotals.forEach((value, category) => {
              if (value > maxValue) {
                maxValue = value;
                maxCategory = category;
              }
            });

            return {
              month: `${this.monthNames[date.getMonth()]}/${date.getFullYear()}`,
              year: date.getFullYear(),
              categoryTotals,
              total,
              maxCategory,
              maxValue
            };
          });

          // Coletar todas as categorias únicas
          const categoriesSet = new Set<string>();
          this.monthsData.forEach(monthData => {
            monthData.categoryTotals.forEach((_, category) => {
              categoriesSet.add(category);
            });
          });
          this.allCategories = Array.from(categoriesSet).sort();

          this.loading = false;
        }).catch(err => {
          console.error('Erro ao carregar dados:', err);
          this.error = 'Erro ao carregar dados dos últimos 3 meses';
          this.loading = false;
        });
      },
      error: (err) => {
        console.error('Erro ao carregar categorias:', err);
        this.error = 'Erro ao carregar categorias';
        this.loading = false;
      }
    });
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getCategoryValue(monthData: MonthData, category: string): number {
    return monthData.categoryTotals.get(category) || 0;
  }

  isMaxCategory(monthData: MonthData, category: string): boolean {
    return monthData.maxCategory === category && monthData.maxValue > 0;
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
