import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { CustomDesignOptions } from '../model/CustomDesignOptions.model';

@Injectable({
  providedIn: 'root'
})
export class CustomDesignOptionsService {
  private path: string;
  private headers: HttpHeaders;
  private options: {
    headers: HttpHeaders
  };
  constructor(private http: HttpClient,
              private _authService: AuthService) {
      this.path = 'http://ichirokuxvi.sytes.net:8080/jerseyuserapi/webapi/userOptions';
      this.headers = new HttpHeaders();
      this.headers = this.headers.append('Content-Type', 'application/x-www-form-urlencoded');
      this.options = { headers: this.headers };
  }

  public getOptions() {
    return this.http.get<CustomDesignOptions[]>(`${this.path}`, this.options);
  }
}
