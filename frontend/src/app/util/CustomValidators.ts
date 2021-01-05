import { FormGroup, ValidationErrors, AbstractControl, Validators } from '@angular/forms';

export class CustomValidators extends Validators {
    /**
     * Checks if two inputs have the same value
     * Used in register and update
     */
    public static matchValues(matchTo: string): (AbstractControl) => ValidationErrors | null {
        return (control: AbstractControl): ValidationErrors | null => {
          return !!control.parent &&
            !!control.parent.value &&
            control.value === control.parent.controls[matchTo].value
            ? null
            : { isMatching: false };
        };
    }
}