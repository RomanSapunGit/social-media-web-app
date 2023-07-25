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
import {GoogleLoginProvider, GoogleSigninButtonModule, SocialAuthServiceConfig} from "@abacritt/angularx-social-login";
import {TimestampDatePipe} from "./pipe/timestamp-date.pipe";
import {CommonModule} from "@angular/common";
import { CommentActionComponent } from './component/dialog/creation-form/comment-action.component';
import {MatDialogModule} from "@angular/material/dialog";
import { ViewFormComponent } from './component/dialog/view-form/view-form.component';
import {CdkDrag, CdkDragHandle} from "@angular/cdk/drag-drop";
import { DraggableDirectiveDirective } from './directive/draggable-directive.directive';
import {CONTENT_TOKEN, CURRENT_PAGE, Page, PAGES, TOTAL_TOKEN} from "./model/page.model";
import { PostComponent } from './component/data/post/post.component';
import { CommentComponent } from './component/data/comment/comment.component';
import { ImageComponent } from './component/data/image/image.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    MainPageComponent,
    PageNotFoundComponent,
    ResetPasswordComponent,
    TimestampDatePipe,
    CommentActionComponent,
    ViewFormComponent,
    DraggableDirectiveDirective,
    PostComponent,
    CommentComponent,
    ImageComponent,
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
        CommonModule,
        MatDialogModule,
        CdkDrag,
        CdkDragHandle,
    ],
  providers: [
    { provide: CONTENT_TOKEN, useValue: [] },
    { provide: TOTAL_TOKEN, useValue: 0 },
    { provide: CURRENT_PAGE, useValue: 0 },
    { provide: PAGES, useValue: 0 },
    { provide: HTTP_INTERCEPTORS,
      useClass: ServerErrorInterceptor,
      multi: true },
    AuthGuard,
    CookieService,
    Page,
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
  bootstrap: [AppComponent],
})
export class AppModule { }
