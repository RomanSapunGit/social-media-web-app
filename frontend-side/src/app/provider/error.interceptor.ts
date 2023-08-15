import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, Observable, throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {NotificationService} from "../service/notification.service";
import {RequestService} from "../service/request.service";
import {AuthService} from "../service/auth.service";

@Injectable()
export class ServerErrorInterceptor implements HttpInterceptor {

  constructor(private snackBarService: NotificationService, private requestService: RequestService,
              private authService: AuthService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        const errorMessage = error.error.message;
        switch (error.status) {
          case 401:
            if (errorMessage.startsWith('JWT expired')) {
              const token = this.authService.getAuthToken();
              const username = this.authService.getUsername();
              this.requestService.refreshToken(token, username);
            } else {
              const notificationMessage = errorMessage.endsWith('because "userEntity" is null')
                ? 'Bad credentials: Wrong username'
                : errorMessage;
              this.snackBarService.showNotification(notificationMessage, true);
            }
            break;
          case 500:
            this.snackBarService.showNotification(errorMessage, true);
            break;
          default:
            this.snackBarService.showNotification(errorMessage, true);
            break;
        }
        return throwError(() => error);
      })
    );
  }
}
