import {ChangeDetectorRef, Component, NgZone, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {NotificationService} from "../../services/notification.service";
import {GoogleLoginProvider, SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {AuthService} from "../../services/auth.service";
import {Subscription} from "rxjs";
import {ServerSendEventService} from "../../services/server-send-event.service";
import {Router} from "@angular/router";
import {CredentialsService} from "../../services/credentials.service";

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

    constructor(private formBuilder: FormBuilder,
                private notificationService: NotificationService, private socialAuthService: SocialAuthService,
                private authService: AuthService, private changeDetectorRef: ChangeDetectorRef,
                private router: Router, private credentialsService: CredentialsService) {
        this.errorMessage = '';
        this.isErrorMessage = false;
        this.loginForm = this.formBuilder.group({
            password: ['', [Validators.required, Validators.minLength(6)]],
            username: ['', [Validators.required, Validators.minLength(3)]]
        });
    }

    async login() {
        console.log('check')
        this.loginData = {...this.loginForm.value};
        this.credentialsService.loginAndRedirect(this.loginData)
    }

    async ngOnInit() {
        let isLoggedIn = localStorage.getItem('isLoggedIn')
        if (isLoggedIn) {
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
                this.credentialsService.loginViaGoogleAndRedirect(this.socialUser.idToken);
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
