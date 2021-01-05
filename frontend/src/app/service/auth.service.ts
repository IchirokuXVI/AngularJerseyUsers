import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient, HttpBackend } from '@angular/common/http';
import { User } from '../model/User.model';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import { reject, resolve } from 'q';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject: BehaviorSubject<User>;
  private _user: Observable<User>;
  private accessTokenSubject: BehaviorSubject<string>;
  private _accessToken: Observable<string>;
  private refreshTokenSubject: BehaviorSubject<string>;
  private _refreshToken: Observable<string>;
  private path: string;
  private headers: HttpHeaders;
  private options: {
    headers: HttpHeaders
  };

  constructor(private http: HttpClient, private router: Router) {
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
    this._user = this.userSubject.asObservable();
    this.accessTokenSubject = new BehaviorSubject<string>(localStorage.getItem('accessToken'));
    this._accessToken = this.accessTokenSubject.asObservable();
    this.refreshTokenSubject = new BehaviorSubject<string>(localStorage.getItem('refreshToken'));
    this._refreshToken = this.refreshTokenSubject.asObservable();
    this.path = 'http://ichirokuxvi.sytes.net:8080/jerseyuserapi/webapi/auth';
    this.headers = new HttpHeaders();
    this.headers = this.headers.append('Content-Type', 'application/x-www-form-urlencoded');
    this.options = { headers: this.headers };
  }

  private storeUserInfo(accessToken: string, refreshToken: string) {
    let token: any = JSON.parse(atob(accessToken.split(".")[1]));
    this.userSubject.next({
      id: token.userId,
      username: token.sub
    });
    this.accessTokenSubject.next(accessToken);
    this.refreshTokenSubject.next(refreshToken);
    localStorage.setItem("user", JSON.stringify(this.user));
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
  }
  
  public updateUser(user: User) {
    this.userSubject.next(user);
    localStorage.setItem("user", JSON.stringify(this.user));
  }

  public login(user: User): Observable<any> {
    let customHeaders = this.headers.append("X-Skip-Interceptor", ''); // Custom header to skip HttpInterceptor
    let customOptions = {headers: customHeaders};
    return this.http.post<any>(
      `${this.path}`,
      `grant_type=password&username=${user.username}&password=${user.password}`,
      customOptions
    ).pipe(
      map(resp => {
        if (resp.accessToken && resp.refreshToken) this.storeUserInfo(resp.accessToken, resp.refreshToken);
        return resp;
      })
    );
  }
  
  public checkToken(): Observable<any> {
    return this.http.get<any>(
      `${this.path}`,
      this.options
    )
  }

  public refreshAccessToken(): Observable<any> {
    let customHeaders = this.headers.append("X-Skip-Interceptor", ''); // Custom header to skip HttpInterceptor
    customHeaders = customHeaders.append("Authorization", "Bearer " + this.refreshToken);
    let customOptions = {headers: customHeaders};
    console.log("startrefresh");
    return this.http.post<any>(
      `${this.path}`,
      `grant_type=refresh_token`,
      customOptions
    )
    .pipe(
      map((resp) => {
        this.storeUserInfo(resp.accessToken, resp.refreshToken);
        return resp;
      }), catchError((err) => {
        return throwError(err);
      })
    )
    // .toPromise().then((resp) => {
    //   this.storeUserInfo(resp.accessToken, resp.refreshToken);
    //   console.log("token refreshed");
    //   return resp;
    // }, (err) => {
    //   return err;
    // });
  }

  public logout() {
    this.userSubject.next(null);
    this.accessTokenSubject.next(null);
    this.refreshTokenSubject.next(null);
    localStorage.removeItem("user");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    this.router.navigate([""]);
  }

  get user(): User {
    return this.userSubject.value;
  }

  get accessToken(): string {
    return this.accessTokenSubject.value;
  }

  get refreshToken(): string {
    return this.refreshTokenSubject.value;
  }

}
