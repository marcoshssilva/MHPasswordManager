import { NgModule } from '@angular/core';
import { ClientPageRoutingModule } from './client-routing.module';

import { ClientPage } from './client.page';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [
    SharedModule,
    ClientPageRoutingModule
  ],
  declarations: [ClientPage]
})
export class ClientPageModule {}
