import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth-guard/auth.guard';
import { MenuGuard } from './core/guards/menu-guard/menu.guard';

const routes: Routes = [
  {
    path: 'authorize',
    canActivate: [MenuGuard],
    loadChildren: () => import('./authorize/authorize.module').then( m => m.AuthorizePageModule)
  },
  {
    path: 'client',
    canActivate: [AuthGuard, MenuGuard],
    loadChildren: () => import('./client/client.module').then( m => m.ClientPageModule)
  },
  {
    path: '',
    redirectTo: 'client',
    pathMatch: 'full'
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
