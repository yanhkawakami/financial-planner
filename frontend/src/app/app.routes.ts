import { Routes } from '@angular/router';
import { SpendListComponent } from './components/spend-list/spend-list.component';
import { SpendFormComponent } from './components/spend-form/spend-form.component';
import { MonthlySummaryComponent } from './components/monthly-summary/monthly-summary.component';
import { QuarterlyComparisonComponent } from './components/quarterly-comparison/quarterly-comparison.component';
import { LoginComponent } from './components/login/login.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SignupComponent } from './components/signup/signup.component';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/spends', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password/:token', component: ResetPasswordComponent },
  { 
    path: 'profile', 
    component: ProfileComponent, 
    canActivate: [authGuard] 
  },
  { 
    path: 'spends', 
    component: SpendListComponent, 
    canActivate: [authGuard] 
  },
  { 
    path: 'spends/quarterly', 
    component: QuarterlyComparisonComponent, 
    canActivate: [authGuard] 
  },
  { 
    path: 'spends/monthly', 
    component: MonthlySummaryComponent, 
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
