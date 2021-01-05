import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotAuthGuard implements CanActivate {

  constructor(private _authServ: AuthService, private router: Router) {
    
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      if (!this._authServ.refreshToken) return true;
      console.log((this._authServ.checkToken().toPromise().then(
        () => {
          this.router.navigate(["/home"]);
          return false;
        },
        () => {
          return true;
        }
        ))
      )
      return true;
    }
}
