import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { SpendService } from '../../services/spend.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { Spend, SpendUpdate } from '../../models/spend.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-spend-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './spend-form.component.html',
  styleUrls: ['./spend-form.component.css']
})
export class SpendFormComponent implements OnInit {
  spend: Spend = {
    spendDate: '',
    spendValue: 0,
    description: '',
    categoryId: 0,
    userId: 0 // Será definido automaticamente no ngOnInit
  };
  
  categories: Category[] = [];
  isEditMode: boolean = false;
  spendId: number | null = null;
  loading: boolean = false;
  error: string = '';
  submitted: boolean = false;

  constructor(
    private spendService: SpendService,
    private categoryService: CategoryService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Definir userId do usuário logado
    const userId = this.authService.getCurrentUserId();
    console.log('👤 User ID obtido do AuthService:', userId);
    if (userId) {
      this.spend.userId = userId;
      console.log('✅ User ID definido no spend:', this.spend.userId);
    } else {
      console.log('❌ Nenhum User ID encontrado - usuário pode não estar logado');
    }
    
    this.loadCategories();
    
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.spendId = +params['id'];
        console.log('📝 Modo de edição ativado para despesa ID:', this.spendId);
        this.loadSpend(this.spendId);
      } else {
        console.log('➕ Modo de criação ativado');
        // Definir a data atual como padrão apenas no modo criação
        const today = new Date();
        this.spend.spendDate = today.toISOString().split('T')[0];
      }
    });
  }

  loadCategories(): void {
    this.categoryService.getCategories(0, 100).subscribe({
      next: (response) => {
        this.categories = response.content;
      },
      error: (err) => {
        this.error = 'Erro ao carregar categorias.';
        console.error('Erro ao carregar categorias:', err);
      }
    });
  }

  loadSpend(id: number): void {
    this.loading = true;
    console.log('🔍 Carregando dados da despesa ID:', id);
    
    this.spendService.getSpendById(id).subscribe({
      next: (spend) => {
        console.log('📄 Dados da despesa carregados:', spend);
        
        // Preencher o formulário com os dados da despesa
        this.spend = {
          id: spend.id,
          spendDate: spend.spendDate,
          spendValue: spend.spendValue,
          description: spend.description,
          categoryId: spend.categoryId,
          userId: spend.userId
        };
        
        console.log('✅ Formulário preenchido com:', this.spend);
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Erro ao carregar despesa:', err);
        this.error = 'Erro ao carregar despesa. Por favor, tente novamente.';
        this.loading = false;
        
        // Se não conseguir carregar, voltar para lista
        setTimeout(() => {
          this.router.navigate(['/spends']);
        }, 2000);
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    
    if (!this.isFormValid()) {
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.isEditMode && this.spendId) {
      const spendUpdate: SpendUpdate = {
        spendDate: this.spend.spendDate,
        spendValue: this.spend.spendValue,
        description: this.spend.description,
        categoryId: this.spend.categoryId
      };

      console.log('✏️ Atualizando despesa ID:', this.spendId, 'com dados:', spendUpdate);
      
      this.spendService.updateSpend(this.spendId, spendUpdate).subscribe({
        next: (updatedSpend) => {
          console.log('✅ Despesa atualizada com sucesso:', updatedSpend);
          this.loading = false;
          this.router.navigate(['/spends']);
        },
        error: (err) => {
          console.error('❌ Erro ao atualizar despesa:', err);
          this.error = 'Erro ao atualizar despesa. Por favor, tente novamente.';
          this.loading = false;
        }
      });
    } else {
      console.log('➕ Criando nova despesa com dados:', this.spend);
      
      this.spendService.createSpend(this.spend).subscribe({
        next: (createdSpend) => {
          console.log('✅ Despesa criada com sucesso:', createdSpend);
          this.loading = false;
          this.router.navigate(['/spends']);
        },
        error: (err) => {
          console.error('❌ Erro ao criar despesa:', err);
          this.error = 'Erro ao criar despesa. Por favor, tente novamente.';
          this.loading = false;
        }
      });
    }
  }

  isFormValid(): boolean {
    return !!(
      this.spend.spendDate &&
      this.spend.spendValue > 0 &&
      this.spend.description &&
      this.spend.categoryId > 0
    );
  }

  cancel(): void {
    this.router.navigate(['/spends']);
  }
}
