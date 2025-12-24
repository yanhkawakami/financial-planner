import { Routes } from '@angular/router';
import { SpendListComponent } from './components/spend-list/spend-list.component';
import { SpendFormComponent } from './components/spend-form/spend-form.component';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/spends', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'spends', 
    component: SpendListComponent, 
    canActivate: [authGuard] 
  },
  { 
    path: 'spends/new', 
    component: SpendFormComponent, 
    canActivate: [authGuard] 
  },
  { 
    path: 'spends/edit/:id', 
    component: SpendFormComponent, 
    canActivate: [authGuard] 
  }
];
