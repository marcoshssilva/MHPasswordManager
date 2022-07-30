import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-bank-cards',
  templateUrl: './bank-cards.page.html',
  styleUrls: ['./bank-cards.page.scss'],
})
export class BankCardsPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) { }

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }

}
