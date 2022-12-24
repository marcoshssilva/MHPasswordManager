import { Component, OnInit } from '@angular/core';
import { MhMenuHelperService } from '../core/services/mh-menu-helper.service';

@Component({
  selector: 'app-client',
  templateUrl: './client.page.html',
  styleUrls: ['./client.page.scss'],
})
export class ClientPage implements OnInit {

  constructor(
    private menuHelper: MhMenuHelperService
  ) { }

  get showMenu() {
    return this.menuHelper.isShowingMenu();
  }

  ngOnInit() {
  }

}
