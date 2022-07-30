import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { PasswordCheckPageRoutingModule } from './password-check-routing.module';

import { PasswordCheckPage } from './password-check.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    PasswordCheckPageRoutingModule,
    SharedModule
  ],
  declarations: [PasswordCheckPage]
})
export class PasswordCheckPageModule {}
