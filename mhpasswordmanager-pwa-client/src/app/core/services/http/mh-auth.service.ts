import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MhAuthService {
  authorizationServerUrl = '';
  clientId = '';
  clientSecret = '';
  host = '';

  constructor(
    private http: HttpClient
  ) {
    this.authorizationServerUrl = environment.authorizationServerUrl;
    this.clientId = environment.clientId;
    this.clientSecret = environment.clientSecret;
  }

  public getTokenWithAuthorizationCode(code: string) {
    const httpHeaders = new HttpHeaders(
      { Authorization: this.generateBasicAuthHeader(),'Content-Type':'application/x-www-form-urlencoded' }
    );
    return this.http.post(
      `${this.authorizationServerUrl}`,
      this.generateBodyAuthorizationCode(code),
      { headers: httpHeaders, observe: 'response' }
    );
  }

  private generateBasicAuthHeader() {
    return 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`);
  }

  private generateBodyAuthorizationCode(code: string) {
    // eslint-disable-next-line prefer-const
    let body: URLSearchParams = new URLSearchParams();
    body.set('grant_type', 'authorization_code');
    body.set('scope', 'user:canRead user:canSelfWrite');
    body.set('code', code);
    body.set('redirect_uri', this.getRedirectUri());
    return body;
  }

  private getRedirectUri() {

    const hostname = window.location.hostname;
    const port = window.location.port;

    switch (port) {
      case '443':
        return 'https://' + hostname + '/authorize';
      case '80':
        return 'http://' + hostname + '/authorize';
      default:
        return 'http://' + hostname + ':' + port + '/authorize';
    }
  }

}
