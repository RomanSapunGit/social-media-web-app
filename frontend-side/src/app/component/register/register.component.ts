import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../service/request.service";
import {SnackBarService} from "../../service/snackbar.service";
import {SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerData = {email: '', password: '', name: '', username: ''};
  errorMessage = '';
  registerForm: FormGroup;
  socialUser!: SocialUser;
  authSubscription!: Subscription
  image = 'assets/image/bg1.jpg'

  constructor(private requestService: RequestService, private formBuilder: FormBuilder,
              private snackBarService: SnackBarService, private socialAuthService: SocialAuthService) {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.minLength(12)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      name: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit() {
    this.snackBarService.errorMessage$.subscribe(message => {
      if (message.includes('Duplicate entry') && message.includes("'users.username'")) {
        this.errorMessage = 'Username already exists. Please choose a different username.';
      } else if (message.includes('Duplicate entry') && message.includes("'users.email'")) {
        this.errorMessage = "Email already exists. Please choose a different email.";
      } else {
        this.errorMessage = message;
      }
    });
    this.authSubscription = this.socialAuthService.authState.subscribe((user) => {
      this.socialUser = user;
      if (this.socialUser && this.socialUser.email) {
        this.registerForm.controls['email'].setValue(user.email);
        this.registerForm.controls['username'].setValue(user.name);
        this.registerForm.controls['name'].setValue(user.name);
      }
    });
  }

  closeError(): void {
    this.errorMessage = '';
  }

  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  register() {
    this.registerData = {...this.registerForm.value};
    this.requestService.register(this.registerData).subscribe({
        error: (error: any) =>
          console.log('Error during registration: ' + error.error.message),
        complete: () => {
          this.requestService.loginAndRedirect(this.registerData);
        }
      }
    );
  }
}
