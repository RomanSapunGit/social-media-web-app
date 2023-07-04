import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {AuthService} from "./auth.service";
import {TokenModel} from "../model/token.model";

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private baseUrl = 'http://localhost:8080';
  private bearerHeader: string;

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) {
    this.bearerHeader = '';
  }

  login(loginData: { username: string, password: string }) {
    return this.http.post(`${this.baseUrl}/token`, loginData);
  }

  loginAndRedirect(loginData: { username: string, password: string }): void {
    this.login(loginData).subscribe(
      {
        next: (response: any) => {
          const token: string = (response as TokenModel).token;
          this.authService.setAuthData(token, loginData.username)
        },
        error: (error: any) =>
          console.log('Error during login ' + error.error.message),
        complete: () =>
          this.router.navigate(['/main']).then(() => {
            console.log('Redirected to main page');
          })
      }
    );
  }

  register(registerData: { email: string, password: string }) {
    return this.http.post(`${this.baseUrl}/account`, registerData);
  }

  registerAndRedirect(registerData: { email: string; password: string }): void {
    this.register(registerData).subscribe(
      {
        error: (error: any) =>
          console.log('Error during registration: ' + error.error.message),
        complete: () =>
          this.router.navigate(['/login']).then(() => {
              console.log('Redirected to login page');
            }
          )
      }
    );
  }

  loginViaGoogle(token: string) {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${this.baseUrl}/token/account`,null, {headers});
  }

  loginViaGoogleAndRedirect(token: string): void {
    this.loginViaGoogle(token).subscribe(
      {
        next: (response: any) => {
          const token: string = (response as TokenModel).token;
          this.authService.setAuthData(token, (response as TokenModel).username)
        },
        error: (error: any) =>
          console.log('Error during login ' + error.error.message),
        complete: () =>
          this.router.navigate(['/main']).then(() => {
            console.log('Redirected to main page');
          })
      }
    );
  }

   validateToken(token: string | null, username: string | null): Observable<boolean> {
    if (username === null || token === null) {
      return of(false);
    }

     const headers = new HttpHeaders()
       .set('Authorization', `Bearer ${token}`)
    return this.http.get<boolean>(`${this.baseUrl}/token/${username}`, {headers});
  }

   refreshToken(token: string | null, username: string | null): void {
    this.http.put<string>(`${this.baseUrl}/token`, {token, username}).subscribe(
      {
        next: (response: any) => {
          const token: string = (response as TokenModel).token;
          this.authService.setAuthToken(token)
        },
        error: (error: any) =>
          console.log('Error during refreshing token: ' + error.error.message),
      }
    );
  }

  forgotPassword(email: string) {
    return this.http.post<void>(`${this.baseUrl}/account/${email}`, {});
  }

  resetPassword(token: string, password: string, matchPassword: string) {
    return this.http.put<void>(`${this.baseUrl}/account/${token}`, {password, matchPassword});
  }
}
