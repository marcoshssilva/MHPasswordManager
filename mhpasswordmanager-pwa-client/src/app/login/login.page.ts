import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MhAuthService } from '../core/services/http/mh-auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  loginForm: FormGroup;
  constructor(
    private authHelper: MhAuthService
  ) { }

  async ngOnInit() {
    this.loginForm = new FormGroup({
      username: new FormControl('ClientWithClientCredentials', [Validators.minLength(6), Validators.required]),
      password: new FormControl('password', [Validators.minLength(6), Validators.required])
    });
  }

  onSubmit() {
    alert('Very well');
  }

}
