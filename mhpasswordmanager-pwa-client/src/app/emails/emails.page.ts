import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-emails',
  templateUrl: './emails.page.html',
  styleUrls: ['./emails.page.scss'],
})
export class EmailsPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) { }

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }

}
