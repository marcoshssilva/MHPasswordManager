import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AllEntriesPage } from './all-entries.page';

const routes: Routes = [
  {
    path: '',
    component: AllEntriesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AllEntriesPageRoutingModule {}
