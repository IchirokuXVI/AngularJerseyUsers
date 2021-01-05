import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { User } from '../model/user.model';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { CustomDesignOptions } from '../model/CustomDesignOptions.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private path: string;
  private headers: HttpHeaders;
  private options: {
    headers: HttpHeaders
  };
  private userOptionsSubject: BehaviorSubject<CustomDesignOptions[]>;
  private _userOptions: Observable<CustomDesignOptions[]>;
  constructor(private http: HttpClient,
              private _authService: AuthService) {
      this.path = 'http://ichirokuxvi.sytes.net:8080/jerseyuserapi/webapi/users';
      this.headers = new HttpHeaders();
      this.headers = this.headers.append('Content-Type', 'application/x-www-form-urlencoded');
      this.options = { headers: this.headers };
      this.userOptionsSubject = new BehaviorSubject(JSON.parse(localStorage.getItem("userOptions")));
      this._userOptions = this.userOptionsSubject.asObservable();
  }

  public getUser(id: string): Observable<User> {
    return this.http.get<User>(`${this.path}`);
  }

  public register(user: User): Observable<boolean> {
    return this.http.post<boolean>(
      `${this.path}`,
      `username=${user.username}&password=${user.password}`,
      this.options
    );
  }

  public update(password: string, newUsername: string, newPassword: string): Observable<any> {
    let encodedUrl = `password=${password}`;
    if (newUsername) {
      encodedUrl += `&newUsername=${newUsername}`;
    }
    if (newPassword) {
      encodedUrl += `&newPassword=${newPassword}`;
    }
    return this.http.put<any>(
      `${this.path}`,
      encodedUrl,
      this.options
    ).pipe(
      map((resp) => {
        // Setting manually id and username because resp has the password, this way the password isn't stored in local storage
        return this._authService.refreshAccessToken();
      })
    )
  }

  private saveOptions(options: CustomDesignOptions[]) {
      // Server returns options with description and we don't need it so remove it before using the data
      for (let i = 0; i < options.length; i++) {
        delete options[i].description;
      }
      localStorage.setItem("userOptions", JSON.stringify(options));
      this.userOptionsSubject.next(options);
  }

  public getOptions(): Observable<any> {
    return this.http.get<CustomDesignOptions[]>(
      `${this.path}/options`,
      this.options
    ).pipe(
      map((options) => {
        this.saveOptions(options);
        return options;
      })
    );
  }

  public updateOptions(options: number[]): Observable<any> {
    let urlEncodedOptions: string =  "";
    options.forEach((value) => {
      urlEncodedOptions += `options=${value}&`;
    });
    return this.http.post<CustomDesignOptions[]>(
      `${this.path}/options`,
      urlEncodedOptions,
      this.options
    ).pipe(
      map((resp) => {
        this.saveOptions(resp);
        return resp;
      })
    )
  }
  
  public get userOptions() {
    return this.userOptionsSubject.value;
  }
}
