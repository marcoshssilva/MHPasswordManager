import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { MhMenuHelperService } from '../../services/mh-menu-helper.service';

@Injectable({
  providedIn: 'root'
})
export class MenuGuard implements CanActivate {
  constructor(
    private menuServiceHelper: MhMenuHelperService
  ){ }


  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      if (route.routeConfig.path.startsWith('login')) {
        this.menuServiceHelper.hiddenMenu();
      } else {
        this.menuServiceHelper.showMenu();
      }

    return true;
  }

}
