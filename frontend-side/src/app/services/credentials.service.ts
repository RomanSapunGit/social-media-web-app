import {Injectable} from '@angular/core';
import {RequestService} from "./request.service";
import {Router} from "@angular/router";
import {NotificationService} from "./notification.service";
import {TokenModel} from "../model/token.model";
import {delay} from "rxjs";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";

@Injectable({
    providedIn: 'root'
})
export class CredentialsService {

    constructor(private requestService: RequestService, private router: Router, private notificationService: NotificationService,
                private breakpointObserver: BreakpointObserver) {
    }

    registerAndRedirect(registerData: FormData): void {
        this.requestService.register(registerData).subscribe(
            {
                next: (response: any) => {
                    if (response.username) {
                        this.router.navigate(['/main']).then(() => {
                            console.log('Redirected to main page');
                        })
                    } else {
                        this.notificationService.showNotification('username or email already exists', true);
                    }
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message)
            }
        );
    }

    loginAndRedirect(loginData: { username: string, password: string }): void {
        this.requestService.login(loginData).subscribe(
            {
                next: (response: any) => {
                    if (response.username) {
                        console.log(response.username)
                        localStorage.setItem('isMobileView', JSON.stringify(this.breakpointObserver.isMatched(Breakpoints.Handset)));
                        localStorage.setItem("username", response.username);
                        this.router.navigate(['/main']).then(() => {
                            console.log('Redirected to main page');
                        })
                    } else {
                        this.notificationService.showNotification('Wrong username or password', true);
                    }
                }
            })
    }

    loginViaGoogleAndRedirect(token: string): void {
        this.requestService.loginViaGoogle(token).pipe(delay(1000)).subscribe(
            {
                next: (response: any) => {
                    localStorage.setItem('isMobileView', JSON.stringify(this.breakpointObserver.isMatched(Breakpoints.Handset)));
                    console.log(response.username)
                        localStorage.setItem("username", response.username);
                        this.router.navigate(['/main'])
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message)
            }
        );
    }
}
