import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { WebsitesPage } from './websites.page';

const routes: Routes = [
  {
    path: '',
    component: WebsitesPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class WebsitesPageRoutingModule {}
