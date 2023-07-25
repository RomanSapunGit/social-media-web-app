import {Injectable} from '@angular/core';
import {Router} from "@angular/router";
import {CookieService} from 'ngx-cookie-service';
import {SocialAuthService} from "@abacritt/angularx-social-login";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authTokenCookieName = 'authToken';
  private usernameCookieName = 'username';
  private isGoogleAccountName = 'is_google_account';

  constructor(private router: Router, private cookieService: CookieService, private socialService: SocialAuthService) {
  }

  private encodeToken(token: string): string {
    return btoa(token);
  }

  public decodeToken(encodedToken: string | null): string {
    if (encodedToken === null) {
      return "";
    }
    return atob(encodedToken);
  }

  public setIsGoogleAccount(): void {
    this.cookieService.set(this.isGoogleAccountName ,'true')
  }
  public getIsGoogleAccount(): string {
   return  this.cookieService.get(this.isGoogleAccountName)
  }

  public setAuthData(token: string, username: string): void {
    this.cookieService.set(this.authTokenCookieName, this.encodeToken(token));

    this.cookieService.set(this.usernameCookieName, username);
  }

  public setAuthToken(token: string): void {
    this.cookieService.set(this.authTokenCookieName, this.encodeToken(token));
  }

  public getAuthToken(): string | null {
    const encodedToken = this.cookieService.get(this.authTokenCookieName);
    return this.decodeToken(encodedToken)
  }

  public getUsername(): string | null {
    return this.cookieService.get(this.usernameCookieName);
  }

  public clearAuthData(): void {
    this.cookieService.delete(this.authTokenCookieName);
    this.cookieService.delete(this.usernameCookieName);
  }
  public clearIsGoogleAccount(): void {
    this.cookieService.delete(this.isGoogleAccountName)
  }

  public logout(): void {
    let authToken = this.getAuthToken();
    let username = this.getUsername();
    if(this.getIsGoogleAccount() == 'true') {
      this.socialService.signOut().then(() => console.log("Google user signed out"));
      this.clearIsGoogleAccount()
    }
    if (authToken && username) {
      this.clearAuthData();
      this.router.navigate(['/login']).then(() =>
        console.log("User has been successfully logged out")
      );
    } else {
      console.log("Something went wrong with either username or token");
    }
  }
}
