import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginComponent} from './component/login/login.component';
import {RegisterComponent} from './component/register/register.component';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ServerErrorInterceptor} from "./provider/error.interceptor";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ReactiveFormsModule} from '@angular/forms';
import {MainPageComponent} from './component/main-page/main-page.component';
import {AuthGuard} from "./provider/auth.guard";
import {CookieService} from "ngx-cookie-service";
import {PageNotFoundComponent} from './component/page-not-found/pagenotfound.component';
import {ResetPasswordComponent} from './component/reset-password/reset-password.component';
import {MatButtonModule} from "@angular/material/button";
import {GoogleLoginProvider, GoogleSigninButtonModule, SocialAuthServiceConfig} from "@abacritt/angularx-social-login";
import {CommonModule} from "@angular/common";
import {CommentActionComponent} from './component/dialog/creation-form/comment-action/comment-action.component';
import {MatDialogModule} from "@angular/material/dialog";
import {ProfileFormComponent} from './component/dialog/profile-form/profile-form.component';
import {CdkDrag, CdkDragHandle} from "@angular/cdk/drag-drop";
import {DraggableDirectiveDirective} from './directive/draggable-directive.directive';
import {CONTENT_TOKEN, CURRENT_PAGE, Page, PAGES, TOTAL_TOKEN} from "./model/page.model";
import {PostComponent} from './component/data/post/post.component';
import {CommentComponent} from './component/data/comment/comment.component';
import {ImageComponent} from './component/data/image/image.component';
import {LazyLoadImageModule} from "ng-lazyload-image";
import { NotificationComponent } from './component/notification/notification.component';
import {TimestampDatePipe} from "./pipe/timestamp-date.pipe";
import {environment} from "../environments/environment";
import { NavigationBarComponent } from './component/navigation-bar/navigation-bar.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import { PostActionComponent } from './component/dialog/creation-form/post-action/post-action.component';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { DropDownMenuComponent } from './component/drop-down-menu/drop-down-menu.component';
import {ClickOutsideDirective} from './directive/outside-click.directive';
import { ErrorDialogComponent } from './component/dialog/error-dialog/error-dialog.component'
import {InfiniteScrollModule} from "ngx-infinite-scroll";
import { PostViewComponent } from './component/dialog/view-form/post-view.component';
import { ImageCropperComponent } from './component/image-cropper/image-cropper.component';
import { ImageCropperModule } from 'ngx-image-cropper';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    MainPageComponent,
    PageNotFoundComponent,
    ResetPasswordComponent,
    CommentActionComponent,
    ProfileFormComponent,
    DraggableDirectiveDirective,
    PostComponent,
    CommentComponent,
    ImageComponent,
    NotificationComponent,
    TimestampDatePipe,
    NavigationBarComponent,
    PostActionComponent,
    DropDownMenuComponent,
    ClickOutsideDirective,
    ErrorDialogComponent,
    PostViewComponent,
    ImageCropperComponent,
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
        LazyLoadImageModule,
        MatToolbarModule,
        SlickCarouselModule,
        InfiniteScrollModule,
      ImageCropperModule
    ],
  providers: [
    {provide: CONTENT_TOKEN, useValue: []},
    {provide: TOTAL_TOKEN, useValue: 0},
    {provide: CURRENT_PAGE, useValue: 0},
    {provide: PAGES, useValue: 0},
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ServerErrorInterceptor,
      multi: true
    },
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
            provider: new GoogleLoginProvider(environment.googleClientId),
          },
        ],
      } as SocialAuthServiceConfig,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
