import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import { MhMenuHelperService } from './core/services/mh-menu-helper.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

  constructor(
    private platform: Platform,
    private menuHelper: MhMenuHelperService
  ) {}

  get showMenu() {
    return this.menuHelper.isShowingMenu();
  }

  async ngOnInit() {
    this.platform.ready().then((_) => {});
  }

}
