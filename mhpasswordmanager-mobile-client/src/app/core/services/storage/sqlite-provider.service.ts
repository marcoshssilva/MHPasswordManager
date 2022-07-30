import { Injectable } from '@angular/core';
import { SQLite, SQLiteObject } from '@awesome-cordova-plugins/sqlite/ngx';
import { SQLitePorter } from '@awesome-cordova-plugins/sqlite-porter/ngx';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SqliteProviderService {

  private storage: SQLiteObject;
  private sqlDataSchema: string;

  constructor(
    private sqlite: SQLite,
    private sqlitePorter: SQLitePorter,
    private http: HttpClient
  ) { }

  startDatabase() {
    this.prepareStorage()
        .then(async _ => await this.prepareSchema())
        .then(async _ => await this.prepareDatabase());
  }

  private async prepareStorage() {
    this.storage = await this.sqlite.create({ name: 'data.db', location: 'default'});
  }

  private async prepareSchema() {
    this.http.get('assets/schema.sql', { responseType: 'text' })
        .subscribe(
          response => this.sqlDataSchema = response);
  }

  private async prepareDatabase() {
    this.sqlitePorter.importSqlToDb(this.storage, this.sqlDataSchema);
  }

}
