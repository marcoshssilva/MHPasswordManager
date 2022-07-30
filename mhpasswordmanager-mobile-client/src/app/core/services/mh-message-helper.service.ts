import { Injectable } from '@angular/core';
import { AlertButton, AlertController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { MhTranslateHelperService } from './mh-translate-helper.service';

@Injectable({
  providedIn: 'root',
})
export class MhMessageHelperService {
  constructor(
    private alertController: AlertController,
    private translate: TranslateService,
    private translateHelperService: MhTranslateHelperService
  ) {
    this.translateHelperService.setupTranslateService(this.translate);
  }

  public async showAlertWithOkOnly(htmlHeader: string, htmlMessage: string): Promise<AlertResult> {

    let responseReturn: AlertResult;
    const okMessage = await this.translateHelperService.getKey('messageHelper.ok', this.translate);
    const buttons = [{
        text: okMessage,
        handler: () => {
          responseReturn = { response: 'OK' };
        },
      },
    ];

    const alert = await this.createDefaultAlert(htmlHeader, htmlMessage, buttons);
    await alert.present();

    return responseReturn;
  }

  public async showAlertWithOkCancel(htmlHeader: string, htmlMessage: string): Promise<AlertResult> {

    let responseReturn: AlertResult;
    const okMessage = await this.translateHelperService.getKey('messageHelper.ok', this.translate);
    const cancelMessage = await this.translateHelperService.getKey('messageHelper.cancel', this.translate);
    const buttons = [{
        text: cancelMessage,
        role: 'cancel',
        cssClass: 'secondary',
        handler: () => {
          responseReturn = { response: 'CANCEL' };
        },
      },
      {
        text: okMessage,
        handler: () => {
          responseReturn = { response: 'OK' };
        },
      },
    ];

    const alert = await this.createDefaultAlert(htmlHeader, htmlMessage, buttons);
    await alert.present();

    return responseReturn;
  }

  public async showDefaultMessageCannotUse(htmlMessage?: string) {
    const htmlDefaultTitle = await this.translateHelperService.getKey('messageHelper.functionDoesntWorktTitle', this.translate);
    const htmlDefaultMessage = await this.translateHelperService.getKey('messageHelper.functionDoesntWorkDescription', this.translate);
    // show alert
    this.showAlertWithOkOnly(htmlDefaultTitle, htmlMessage || htmlDefaultMessage);
  }

  private async createDefaultAlert(header: string, message: string,buttons: AlertButton[]) {
    return this.alertController.create({ header, message, buttons: [...buttons] });
  }

}

export interface AlertResult {
  response: 'CANCEL' | 'OK';
}
