import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, switchMap, filter, take, map, finalize } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable()
export class InterceptorService implements HttpInterceptor {
  private refreshTokenInProgress = false;
  private refreshTokenSubject = new BehaviorSubject<any>(null);


  constructor(public _authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.headers.has("X-Skip-Interceptor")) return next.handle(req);
    let request = req.clone();
    if (localStorage.getItem("accessToken")) {
      request = req.clone({
        headers: req.headers.set("Authorization", "Bearer " + this._authService.accessToken)
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Probably the token expired so try to refresh it first
        if (error && error.status === 401 && this._authService.refreshToken) {
          console.log(error.status);
          if (this.refreshTokenInProgress) {
            // Already refreshing token so we have to wait until it is refreshed
            return this.refreshTokenSubject.pipe(
              filter(accessToken => accessToken !== null),
              take(1),
              switchMap((accessToken) => {
                request = req.clone({
                  headers: req.headers.set("Authorization", "Bearer " + accessToken)
                });
                return next.handle(request);
              })
            )
          } else {
            this.refreshTokenInProgress = true;
            this.refreshTokenSubject.next(null);

            return this._authService.refreshAccessToken().pipe(
              switchMap(resp => {
                this.refreshTokenInProgress = false;
                request = req.clone({
                  headers: req.headers.set("Authorization", "Bearer " + resp.accessToken)
                });
                console.log("tokenRefreshed");
                return next.handle(request);
              }),
              catchError(err => {
                if (err.status === 401) {
                  this.refreshTokenInProgress = false;
                  this._authService.logout();
                }
                return throwError(err);
              })
            )


            // this._authService.refreshAccessToken().subscribe(resp => {
            //   this.refreshTokenInProgress = false;
            //   request = req.clone({
            //     headers: req.headers.set("Authorization", "Bearer " + this._authService.accessToken)
            //   });
            //   return next.handle(request);
            // }, err => {
            //   this.refreshTokenInProgress = false;
            //   this._authService.logout();
            //   return throwError(err);
            // })


            // this._authService.refreshAccessToken().pipe(
            //   map(resp => {
            //     this.refreshTokenInProgress = false;
            //     this.refreshTokenSubject.next(resp.accessToken);
            //     request = req.clone({
            //       headers: req.headers.set("Authorization", "Bearer " + this._authService.accessToken)
            //     });
            //     return next.handle(request);
            //   }),
            //   catchError(error => {
            //     // If there is an exception calling 'refreshToken', bad news so logout.
            //     this._authService.logout();
            //     return throwError(error);
            //   }),
            //   finalize(() => {
            //       this.refreshTokenInProgress = false;
            //   })
            // )


            // this._authService.refreshAccessToken().then(
            //   (resp) => {
            //     this.refreshTokenInProgress = false;
            //     this.refreshTokenSubject.next(resp.accessToken);
            //     request = req.clone({
            //       headers: req.headers.set("Authorization", "Bearer " + this._authService.accessToken)
            //     });
            //     return next.handle(request);
            //   }, (err) => {
            //     this.refreshTokenInProgress = false;
            //     this._authService.logout();
            //     return throwError(error);
            //   }
            // )
          }
        } else {
          return throwError(error);
        }
      })
    );
  }
}
