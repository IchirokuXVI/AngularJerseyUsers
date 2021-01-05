import { Component } from '@angular/core';
import { User } from './model/user.model';
import { UserService } from './service/user.service';
import { Router } from '@angular/router';
import { AuthService } from './service/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  constructor(private _authServ: AuthService,
    private _userServ: UserService,
    private router: Router) {}

  ngOnInit(): void {
  }
  
  public checkOption(name: string) {
    if (this.options) {
      for (let option of this.options) {
        if (option.name == name) {
          return true;
        }
      }
      return false;
    }
  }

  get user() {
    return this._authServ.user;
  }

  public get options() {
    return this._userServ.userOptions;
  }
}
