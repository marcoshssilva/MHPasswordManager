import { Component, OnInit } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-mh-avatar-user-principal',
  templateUrl: './mh-avatar-user-principal.component.html',
  styleUrls: ['./mh-avatar-user-principal.component.scss'],
})
export class MhAvatarUserPrincipalComponent implements OnInit {

  constructor(
    private menuController: MenuController,
    private translate: TranslateService,
    private translateHelper: MhTranslateHelperService
  ) { }

  ngOnInit() {
    this.translateHelper.setupTranslateService(this.translate);
  }

  closeMenu() {
    this.menuController.close();
  }
}
