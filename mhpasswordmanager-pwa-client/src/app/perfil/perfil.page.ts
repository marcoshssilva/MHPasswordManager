import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from '../core/services/mh-translate-helper.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.page.html',
  styleUrls: ['./perfil.page.scss'],
})
export class PerfilPage implements OnInit {

  constructor(
    private translate: TranslateService,
    private translateHelper: MhTranslateHelperService
  ) { }

  ngOnInit() {
    this.translateHelper.setupTranslateService(this.translate);
  }

}
