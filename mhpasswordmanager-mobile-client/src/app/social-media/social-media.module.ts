import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { SocialMediaPageRoutingModule } from './social-media-routing.module';

import { SocialMediaPage } from './social-media.page';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SocialMediaPageRoutingModule,
    SharedModule
  ],
  declarations: [SocialMediaPage]
})
export class SocialMediaPageModule {}
