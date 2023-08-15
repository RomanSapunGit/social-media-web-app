import {catchError, map, Observable, of} from "rxjs";
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {Injectable} from "@angular/core";
import {RequestService} from "../service/request.service";
import {MatDialogService} from "../service/mat-dialog.service";

@Injectable()
export class AuthGuard {
errorMessage: string
  constructor(private requestService: RequestService, private authService: AuthService,
              private router: Router, private matDialogService: MatDialogService) {
this.errorMessage = '';
  }

  canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    let token = this.authService.getAuthToken();
    let username = this.authService.getUsername();
    if (token && username) {
      return this.requestService.validateToken(token, username)
        .pipe(
          map((isValid: boolean) => {
              return isValid
          }),
          catchError((error: any) => {
            if (error && error.error && error.error.message && error.error.message.startsWith("JWT expired")) {
              this.matDialogService.displayError('Your session expired, please log in');
              return of(this.router.createUrlTree(['/login']));
            } else {
              return of(false);
            }
          })
        );
    }
    return of(this.router.createUrlTree(['/login']));
  }
}
