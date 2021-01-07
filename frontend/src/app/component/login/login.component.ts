import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { UserService } from '../../service/user.service';
import { Router } from '@angular/router';
import { CustomValidators } from 'src/app/util/CustomValidators';

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
              private router: Router,
              private frmBuilder: FormBuilder) {
    this.form = new FormGroup ({
      username: new FormControl('', {
        validators: Validators.required,
        updateOn: 'submit'
      }),
      password: new FormControl('', {
        validators: Validators.required
      })
    });

    // Confirm password input won't be invalid/valid if you first fill confirm and then password so
    // this way we are forcing it to re-evalidate when password changes
    this.form.controls.password.valueChanges.subscribe(() => {
      if (!this.login) {
        this.form.controls.passwordConfirm.updateValueAndValidity();
      }
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
        $('#confirmPasswordError').text("");
        this._userServ.register(form.value).subscribe(
          resp => {
              $('#formMessage').text("User successfully created");
              $('.is-invalid').removeClass('is-invalid');
              this.form.removeControl("passwordConfirm");
              this.login = true;
          }, err => {
            $('#usernameInput').addClass("is-invalid");
        });
      }
    }
  }

  public secondButtonClick(): void {
    this.login = !this.login;
    if (!this.login) {
      this.form.addControl("passwordConfirm", new FormControl('', {
          validators: CustomValidators.matchValues('password'),
          updateOn: 'change'
        })
      )
    } else {
      this.form.removeControl("passwordConfirm");
    }
    this.form.reset();
    $('#formError').text("");
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
