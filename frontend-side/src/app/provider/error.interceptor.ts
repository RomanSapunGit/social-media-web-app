import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, EMPTY, Observable, throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {NotificationService} from "../services/notification.service";
import {RequestService} from "../services/request.service";
import {AuthService} from "../services/auth.service";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class ServerErrorInterceptor implements HttpInterceptor {

    constructor(private snackBarService: NotificationService, private requestService: RequestService,
                private authService: AuthService, private translateService: TranslateService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                const errorMessage = error.error.message || 'unknown error';
                const message = error.error
                console.log(errorMessage)
                console.log(message)
                if(message && errorMessage === 'unknown error') {
                    this.snackBarService.showNotification('Not authenticated to access this site', true)
                    return EMPTY;
                }
                console.log(error)
                const errorCausedBy = error.error.causedBy;
                const timestamp = error.error.timestamp;
                switch (error.status) {
                    case 401:
                        if (errorMessage.startsWith('JWT expired')) {
                            const token = this.authService.getAuthToken();
                            const username = this.authService.getUsername();
                            this.requestService.refreshToken(token, username);
                        } else {
                            let notificationMessage = '';
                            errorMessage.startsWith('Bad credentials')
                                ? this.translateService.get('BAD_CREDENTIALS').subscribe((translation: string) => {
                                    notificationMessage = translation;
                                }) : notificationMessage = errorMessage;
                            this.snackBarService.showNotification(notificationMessage, true);
                            console.log(errorMessage, errorCausedBy, timestamp)
                        }
                        break;
                    case 500:
                        this.snackBarService.showNotification(errorMessage, true);
                        console.log(errorMessage, errorCausedBy, timestamp)
                        this.snackBarService.showNotification(errorMessage, true);
                        break;
                    case 404:
                        console.log(errorMessage, errorCausedBy, timestamp)
                        break;
                    case 200:
                        break;
                    default:
                        console.log('check')
                        this.snackBarService.showNotification(errorMessage, true);

                        return EMPTY;
                }
                return EMPTY;
            })
        );
    }
}
