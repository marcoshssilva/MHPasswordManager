import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { PasswordGeneratorPage } from './password-generator.page';

const routes: Routes = [
  {
    path: '',
    component: PasswordGeneratorPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PasswordGeneratorPageRoutingModule {}
