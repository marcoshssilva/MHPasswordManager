import { Component, Input, OnInit } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { MhMessageHelperService } from 'src/app/core/services/mh-message-helper.service';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-mh-menu-principal',
  templateUrl: './mh-menu-principal.component.html',
  styleUrls: ['./mh-menu-principal.component.scss'],
})
export class MhMenuPrincipalComponent implements OnInit {

  @Input() setMenuOpen = false;

  content: Component;
  contentID = 'main';
  menuID = 'mh-menu-principal';
  positionMenu = 'start';
  typeMenu = 'overlay';
  menuItems: ItemMenu[];

  constructor(
    private menuController: MenuController,
    private messageHelper: MhMessageHelperService,
    private translate: TranslateService,
    private translateHelper: MhTranslateHelperService
  ) { }

  async ngOnInit() {
    this.translateHelper.setupTranslateService(this.translate);
    this.menuItems = [
      {
        icon: 'folder-outline',
        text: 'menuPrincipal.itemsMenu.allEntries',
        quantity: 10,
        routerRedirect: 'all-entries'
      },
      {
        icon: 'mail-outline',
        text: 'menuPrincipal.itemsMenu.emails',
        quantity: 10,
        routerRedirect: 'emails'
      },
      {
        icon: 'cloud-outline',
        text: 'menuPrincipal.itemsMenu.socialMedia',
        quantity: 10,
        routerRedirect: 'social-media'
      },
      {
        icon: 'globe-outline',
        text: 'menuPrincipal.itemsMenu.websites',
        quantity: 10,
        routerRedirect: 'websites'
      },
      {
        icon: 'layers-outline',
        text: 'menuPrincipal.itemsMenu.applications',
        quantity: 10,
        routerRedirect: 'applications'
      },
      {
        icon: 'card-outline',
        text: 'menuPrincipal.itemsMenu.bankCards',
        quantity: 10,
        routerRedirect: 'bank-cards'
      }
    ];
  }

  public closeMenu() {
    this.menuController.close(this.menuID);
  }

  public clickBtnSettings() {
    this.closeMenu();
    this.messageHelper.showDefaultMessageCannotUse();
  }

  public clickBtnLockApp() {
    this.closeMenu();
    this.messageHelper.showDefaultMessageCannotUse();
  }

  public clickBtnHelp() {
    this.closeMenu();
    this.messageHelper.showDefaultMessageCannotUse();
  }
}

export interface ItemMenu {
  icon: string;
  text: string;
  quantity: number;
  routerRedirect: string;
}
