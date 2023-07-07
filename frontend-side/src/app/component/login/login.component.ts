import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../service/request.service";
import {SnackBarService} from "../../service/snackbar.service";
import { SocialAuthService, SocialUser, GoogleLoginProvider } from "@abacritt/angularx-social-login";
import {AuthService} from "../../service/auth.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginData = {username: '', password: ''};
  loginForm: FormGroup;
  image = 'assets/image/bg1.jpg'
  errorMessage = '';
  authSubscription!: Subscription

  socialUser!: SocialUser;

  constructor(private requestService: RequestService, private formBuilder: FormBuilder,
              private snackBarService: SnackBarService, private socialAuthService: SocialAuthService,
              private authService: AuthService) {
    this.loginForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  login() {
    this.loginData = {...this.loginForm.value};
    this.requestService.loginAndRedirect(this.loginData)
  }

  ngOnInit() {
    this.snackBarService.errorMessage$.subscribe((message) => {
      this.errorMessage = message;
    });
    this.authSubscription = this.socialAuthService.authState.subscribe((user) => {
      this.socialUser = user;
      if (this.socialUser && this.socialUser.idToken) {
          this.requestService.loginViaGoogleAndRedirect(this.socialUser.idToken);
          this.authService.setIsGoogleAccount();
      }
    });
  }

  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  closeError(): void {
    this.errorMessage = '';
  }
}
