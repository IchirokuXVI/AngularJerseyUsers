import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { AuthService } from '../service/auth.service';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private _authServ: AuthService, private router: Router) {
    
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      if (!this._authServ.refreshToken) {
        this.router.navigate([""]);
        return false;
      }
      return this._authServ.checkToken().toPromise().then(
        (resp) => {
          console.log("tokenChecked true");
          return true;
        },
        (err) => {
          console.log("Token expired or an error ocurred");
          this._authServ.logout();
          this.router.navigate([""]);
          return false;
        }
      )
  }
  
}
