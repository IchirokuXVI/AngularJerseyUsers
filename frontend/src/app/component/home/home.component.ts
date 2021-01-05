import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { CustomValidators } from '../../util/CustomValidators';
import { UserService } from 'src/app/service/user.service';
import { CustomDesignOptionsService } from 'src/app/service/customDesignOptions.service';
import { CustomDesignOptions } from '../../model/CustomDesignOptions.model';
import { User } from 'src/app/model/User.model';
import { fbind } from 'q';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  public frm: FormGroup;
  public optionsFrm: FormGroup;
  public unlocked: boolean = false;
  public options;
  private selectedOptions: any[];

  constructor(private frmBuilder: FormBuilder,
    private _authService: AuthService,
    private _userService: UserService,
    private _optionsService: CustomDesignOptionsService ) {
    this.unlocked = false;
    this.frm = new FormGroup({
      newUsername: new FormControl('', {
        validators: Validators.required,
        updateOn: 'submit'
      }),
      password: new FormControl('', {
        validators: Validators.required,
        updateOn: 'submit'
      }),
      newPassword: new FormControl(''),
      passwordConfirm: new FormControl('', {
        validators: CustomValidators.matchValues('newPassword'),
        updateOn: 'change'
      })
    });
    // Set value after creating form so the reactive form keeps track of the change
    // otherwise the form would be always invalid until the user triggers the change event
    // in newUsername input
    this.frm.controls.newUsername.setValue(this.user.username);

    // Confirm password input won't be invalid/valid if you first fill confirm and then password so
    // this way we are forcing it to re-evalidate when password changes
    this.frm.controls.newPassword.valueChanges.subscribe(() => {
      this.frm.controls.passwordConfirm.updateValueAndValidity();
    });

    this.getOptions();
  }

  ngOnInit(): void {
  }

  public toggleEdit() {
    if (this.unlocked) {
      this.frm.reset();
      this.frm.controls.newUsername.setValue(this.user.username);
      $('#passwordError').text('Password is required to edit your account');
      $('.is-invalid').removeClass('is-invalid');
    }
    this.unlocked = !this.unlocked;
  }

  public updateUserInfo(form: FormGroup) {
    if (form.valid) {
      this._userService.update(form.get('password').value, form.get('newUsername').value, form.get('newPassword').value).subscribe(
        (refreshObservable)=>{
          // UserService.update() calls AuthService.refreshAccessToken() and returns it so instead of response we get an Observable
          // toggleEdit must trigger after the refresh and access tokens are refreshed because otherwise the username could be wrong
          // if it was edited and then tried to edit it again
          refreshObservable.subscribe(() => {
            this.toggleEdit();
          });
        },
        (err) => {
          if (err.error == "Username already taken") {
            $('#usernameInput').addClass('is-invalid');
            $('#passwordInput').removeClass('is-invalid');
          } else if (err.error == "Wrong password") {
            $('#passwordInput').addClass('is-invalid');
            $('#passwordError').text('Wrong password');
          }
        });
    }
  }

  public checkboxChange(optionId: number) {
    console.log(optionId);
    if (this.selectedOptions.includes(optionId)) {
      this.selectedOptions.splice(this.selectedOptions.indexOf(optionId), 1);
    } else {
      this.selectedOptions.push(optionId);
    }
    console.log(this.selectedOptions);
  }

  public updateUserOptions(form: FormGroup) {
    console.log(form);
    this._userService.updateOptions(this.selectedOptions).subscribe();
  }

  public getOptions(): void {
    this._optionsService.getOptions().subscribe((options) => {
      // Set all checkboxes to false, later on we will set to true
      // some of them based on user's previously selected options
      this.optionsFrm = this.frmBuilder.group({
        options: this.frmBuilder.array(options.map(option => {
          return this.frmBuilder.control(false)
        }))
      });
      this.options = options;
    });

    this._userService.getOptions().subscribe((options) => {
      this.selectedOptions = options.map((option) => {
        $(`#${this.removeWhiteSpace(option.name)}`).prop('checked', true);
        return option.id;
      })
    });
  }

  public removeWhiteSpace(string: string) {
    return string.replace(/\s/g,'');
  }

  public get user(): User {
    return this._authService.user;
  }

  public get optionsArray(): FormArray {
    return this.optionsFrm.get('options') as FormArray;
  }
}
