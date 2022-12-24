import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { BankCardsPageRoutingModule } from './bank-cards-routing.module';

import { BankCardsPage } from './bank-cards.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    BankCardsPageRoutingModule,
    SharedModule
  ],
  declarations: [BankCardsPage]
})
export class BankCardsPageModule {}
