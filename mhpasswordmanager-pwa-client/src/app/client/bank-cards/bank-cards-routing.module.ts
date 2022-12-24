import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BankCardsPage } from './bank-cards.page';

const routes: Routes = [
  {
    path: '',
    component: BankCardsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BankCardsPageRoutingModule {}
