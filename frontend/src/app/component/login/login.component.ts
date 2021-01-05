import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { UserService } from '../../service/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'div[app-login]',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  form: FormGroup;
  login: boolean = true;

  constructor(private _authServ: AuthService,
              private _userServ: UserService,
              protected router: Router) {
    this.form = new FormGroup ({
      username: new FormControl('', {
        validators: Validators.required,
        updateOn: 'submit'
      }),
      password: new FormControl('', {
        validators: Validators.required,
        updateOn: 'submit'
      })
    });
  }

  ngOnInit(): void {
  }

  public sendForm(form: FormGroup) {
    if (form.valid) {
      if (this.login) {
        this._authServ.login(form.value).subscribe(
          next => {
            this.router.navigate(["/home"]);
          },
          error => {
            $('#formError').text("Invalid username or password");
          }
        );
      } else {
        if ($('#password').val() === $('#confirmPassword').val()) {
          $('#confirmPasswordError').text("");
          this._userServ.register(form.value).subscribe(
            resp => {
                $('#formMessage').text("User successfully created");
                $('#usernameError').text("");
                this.login = true;
            }, err => {
              $('#usernameError').text("Username already taken");
          });
        } else {
          $('#confirmPasswordError').text("Passwords doesn't match");
        }
      }
    }
  }

  public secondButtonClick(): void {
    this.login = !this.login;
    $('#formError').text("");
    $('#usernameError').text("");
    $('#formMessage').text("");
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

  public get options() {
    return this._userServ.userOptions;
  }

}
