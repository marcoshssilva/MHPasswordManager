import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import { SqliteProviderService } from './core/services/storage/sqlite-provider.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {
  constructor(
    private platform: Platform,
    private sqliteProvider: SqliteProviderService
  ) {}

  async ngOnInit() {
    // await app run
    this.platform.ready().then((_) => {
      // initialize database
      this.sqliteProvider.startDatabase();
    });
  }
}
