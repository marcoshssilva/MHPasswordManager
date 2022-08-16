import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MhAuthService {

  host = 'http://localhost:9000/oauth2/token';

  constructor(
    private http: HttpClient
  ) {
  }

  public static generateBasicAuthHeader(clientId: string, clientSecret: string) {
    return 'Basic ' + btoa(`${clientId}:${clientSecret}`);
  }

  public getTokenWithClientCredentials(clientId: string, clientSecret: string) {
    const httpHeaders = new HttpHeaders({
      Authorization: MhAuthService.generateBasicAuthHeader(clientId, clientSecret),
      'Content-Type': 'application/x-www-form-urlencoded',
    });
    const body = 'grant_type=client_credentials&scope=openid';
    return this.http.post<any>(`${this.host}`, body, {headers: httpHeaders, observe: 'response'});
  }

  public getTokenWithAuthorizationCode(code: string, clientId: string, clientSecret: string, redirectUri: string) {
    const httpHeaders = new HttpHeaders({
      Authorization: MhAuthService.generateBasicAuthHeader(clientId, clientSecret),
      'Content-Type': 'application/x-www-form-urlencoded',
    });
    const body = 'grant_type=authorization_code&scope=openid&code=' + code;
    return this.http.post(`${this.host}`, body, {headers: httpHeaders, observe: 'response'});
  }


}
