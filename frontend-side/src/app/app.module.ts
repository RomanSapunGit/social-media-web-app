import { NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ServerErrorInterceptor} from "./provider/error.interceptor";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import {MainPageComponent} from './component/mainpage/mainpage.component';
import {AuthGuard} from "./provider/auth.guard";
import {CookieService} from "ngx-cookie-service";
import { PageNotFoundComponent } from './component/pagenotfound/pagenotfound.component';
import { ResetPasswordComponent } from './component/reset-password/reset-password.component';
import {MatButtonModule} from "@angular/material/button";
import { RegisterViaGoogleComponent } from './component/register-via-google/register-via-google.component';
import {GoogleLoginProvider, GoogleSigninButtonModule, SocialAuthServiceConfig} from "@abacritt/angularx-social-login";
import { LoginViaGoogleComponent } from './component/login-via-google/login-via-google.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    MainPageComponent,
    PageNotFoundComponent,
    ResetPasswordComponent,
    RegisterViaGoogleComponent,
    LoginViaGoogleComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    MatSnackBarModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatButtonModule,
    GoogleSigninButtonModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS,
      useClass: ServerErrorInterceptor,
      multi: true },
    AuthGuard,
    CookieService,
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider('973660462549-isbq8itomqfkjbt7m2rrnht9jv7rt3tn.apps.googleusercontent.com'),
          },
        ],
      } as SocialAuthServiceConfig,
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
