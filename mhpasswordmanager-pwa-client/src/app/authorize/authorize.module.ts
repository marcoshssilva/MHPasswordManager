import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {AuthorizePageRoutingModule} from './authorize-routing.module';

import {AuthorizePage} from './authorize.page';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    AuthorizePageRoutingModule,
    SharedModule
  ],
  declarations: [AuthorizePage]
})
export class AuthorizePageModule {
}
