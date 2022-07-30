import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { AllEntriesPageRoutingModule } from './all-entries-routing.module';

import { AllEntriesPage } from './all-entries.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    AllEntriesPageRoutingModule,
    SharedModule
  ],
  declarations: [AllEntriesPage]
})
export class AllEntriesPageModule {}
