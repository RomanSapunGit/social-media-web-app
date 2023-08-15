import {ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../service/request.service";
import {NotificationService} from "../../service/notification.service";
import {SocialAuthService, SocialUser, GoogleLoginProvider} from "@abacritt/angularx-social-login";
import {AuthService} from "../../service/auth.service";
import {Subscription} from "rxjs";
import {MatDialogService} from "../../service/mat-dialog.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  loginData = {username: '', password: ''};
  loginForm: FormGroup;
  image = 'assets/image/bg1.jpg'
  authSubscription!: Subscription
  errorMessage: string;
  isErrorMessage: boolean;
  socialUser!: SocialUser;

  constructor(private requestService: RequestService, private formBuilder: FormBuilder,
              private notificationService: NotificationService, private socialAuthService: SocialAuthService,
              private authService: AuthService, private changeDetectorRef: ChangeDetectorRef) {
    this.errorMessage = '';
    this.isErrorMessage = false;
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
    this.notificationService.notification$.subscribe((message) => {
      this.errorMessage = (message.message);
      this.isErrorMessage = message.isErrorMessage;
      this.changeDetectorRef.detectChanges();
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
}
