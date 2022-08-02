import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { EmailsPageRoutingModule } from './emails-routing.module';

import { EmailsPage } from './emails.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    EmailsPageRoutingModule,
    SharedModule
  ],
  declarations: [EmailsPage]
})
export class EmailsPageModule {}
