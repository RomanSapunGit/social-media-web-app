import {ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {NotificationService} from "../../services/notification.service";
import {SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {AuthService} from "../../services/auth.service";
import {Subscription} from "rxjs";
import {ServerSendEventService} from "../../services/server-send-event.service";
import {Router} from "@angular/router";

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
              private authService: AuthService, private changeDetectorRef: ChangeDetectorRef,
              private router: Router) {
    this.errorMessage = '';
    this.isErrorMessage = false;
    this.loginForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

 async login() {
    this.loginData = {...this.loginForm.value};
    this.requestService.loginAndRedirect(this.loginData)
  }

 async ngOnInit() {
      if(this.authService.getAuthToken() && this.authService.getUsername()) {
          await this.router.navigate(['/main'])
      }
    this.notificationService.notification$.subscribe((message) => {
      this.errorMessage = (message.message);
      this.isErrorMessage = message.isErrorMessage;
      this.changeDetectorRef.detectChanges();
    });
    this.authSubscription = this.socialAuthService.authState.subscribe(async (user) => {
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
