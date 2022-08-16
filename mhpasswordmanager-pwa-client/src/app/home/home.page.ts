import { Component, OnInit } from '@angular/core';
import { App } from '@capacitor/app';
import { IonRouterOutlet, Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage implements OnInit {
  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService,
    private platform: Platform,
    private routerOutlet: IonRouterOutlet
  ) {}

  async ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
    // exit app when press harware button
    this.platform.backButton.subscribeWithPriority(10, () => {
      if (!this.routerOutlet.canGoBack()){
        App.exitApp();
      } else {
        this.routerOutlet.pop();
      }

    });
  }
}
