import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-applications',
  templateUrl: './applications.page.html',
  styleUrls: ['./applications.page.scss'],
})
export class ApplicationsPage implements OnInit {
  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) {}

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }
}
