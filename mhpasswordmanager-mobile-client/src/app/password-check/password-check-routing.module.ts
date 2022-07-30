import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { PasswordCheckPage } from './password-check.page';

const routes: Routes = [
  {
    path: '',
    component: PasswordCheckPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PasswordCheckPageRoutingModule {}
