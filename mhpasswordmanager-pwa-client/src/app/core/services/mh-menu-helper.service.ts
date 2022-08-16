import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MhMenuHelperService {

  isMenuOpened = true;
  constructor() { }

  hiddenMenu() {
    this.isMenuOpened = false;
  }

  showMenu() {
    this.isMenuOpened = true;
  }

  isShowingMenu() {
    return this.isMenuOpened;
  }
}
