import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-password-generator',
  templateUrl: './password-generator.page.html',
  styleUrls: ['./password-generator.page.scss'],
})
export class PasswordGeneratorPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) { }

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }

}
