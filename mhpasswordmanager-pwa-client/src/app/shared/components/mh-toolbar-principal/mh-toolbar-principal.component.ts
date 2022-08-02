import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-mh-toolbar-principal',
  templateUrl: './mh-toolbar-principal.component.html',
  styleUrls: ['./mh-toolbar-principal.component.scss'],
})
export class MhToolbarPrincipalComponent implements OnInit {

  @Input() title: string;
  @Input() searchButton = false;
  constructor() {}

  async ngOnInit() {}
}
