import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { WebsitesPageRoutingModule } from './websites-routing.module';

import { WebsitesPage } from './websites.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    WebsitesPageRoutingModule,
    SharedModule
  ],
  declarations: [WebsitesPage]
})
export class WebsitesPageModule {}
