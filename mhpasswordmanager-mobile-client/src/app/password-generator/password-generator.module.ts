import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { PasswordGeneratorPageRoutingModule } from './password-generator-routing.module';

import { PasswordGeneratorPage } from './password-generator.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    PasswordGeneratorPageRoutingModule,
    SharedModule
  ],
  declarations: [PasswordGeneratorPage]
})
export class PasswordGeneratorPageModule {}
