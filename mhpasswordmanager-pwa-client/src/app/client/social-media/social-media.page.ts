import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-social-media',
  templateUrl: './social-media.page.html',
  styleUrls: ['./social-media.page.scss'],
})
export class SocialMediaPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) { }

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }

}
