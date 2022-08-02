import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from 'src/app/core/services/mh-translate-helper.service';

@Component({
  selector: 'app-all-entries',
  templateUrl: './all-entries.page.html',
  styleUrls: ['./all-entries.page.scss'],
})
export class AllEntriesPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) { }

  ngOnInit() {
    // setup translator
    this.translateHelperService.setupTranslateService(this.translate);
  }

}
