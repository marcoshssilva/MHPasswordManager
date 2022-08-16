import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth-guard/auth.guard';
import { MenuGuard } from './core/guards/menu-guard/menu.guard';

const routes: Routes = [
  {
    path: 'home',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./home/home.module').then( m => m.HomePageModule)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'password-check',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./password-check/password-check.module').then( m => m.PasswordCheckPageModule)
  },
  {
    path: 'password-generator',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./password-generator/password-generator.module').then( m => m.PasswordGeneratorPageModule)
  },
  {
    path: 'all-entries',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./all-entries/all-entries.module').then( m => m.AllEntriesPageModule)
  },
  {
    path: 'emails',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./emails/emails.module').then( m => m.EmailsPageModule)
  },
  {
    path: 'social-media',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./social-media/social-media.module').then( m => m.SocialMediaPageModule)
  },
  {
    path: 'websites',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./websites/websites.module').then( m => m.WebsitesPageModule)
  },
  {
    path: 'applications',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./applications/applications.module').then( m => m.ApplicationsPageModule)
  },
  {
    path: 'bank-cards',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./bank-cards/bank-cards.module').then( m => m.BankCardsPageModule)
  },
  {
    path: 'perfil',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./perfil/perfil.module').then( m => m.PerfilPageModule)
  },
  {
    path: 'login',
    canActivate: [MenuGuard, AuthGuard],
    loadChildren: () => import('./login/login.module').then( m => m.LoginPageModule)
  },


];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
