import {catchError, map, Observable, of} from "rxjs";
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {Injectable} from "@angular/core";
import {RequestService} from "../service/request.service";

@Injectable()
export class AuthGuard {

  constructor(private requestService: RequestService, private authService: AuthService,
              private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    let token = this.authService.getAuthToken();
    let username = this.authService.getUsername();
    return this.requestService.validateToken(token, username)
      .pipe(
        map((isValid: boolean) => {
          return isValid
        }),
        catchError((error: any) => {
          if (error && error.error && error.error.message && error.error.message.startsWith("JWT expired")) {
            const redirectUrl = state.url;
            return of(this.router.createUrlTree([redirectUrl]));
          } else {
            return of(false);
          }
        })
      );
  }
}
