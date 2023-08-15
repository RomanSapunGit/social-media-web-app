import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from "./component/register/register.component";
import {LoginComponent} from "./component/login/login.component";
import {AuthGuard} from "./provider/auth.guard";
import {MainPageComponent} from "./component/main-page/main-page.component";
import {PageNotFoundComponent} from "./component/page-not-found/pagenotfound.component";
import {ResetPasswordComponent} from "./component/reset-password/reset-password.component";
import {CommonModule} from "@angular/common";

const routes: Routes = [
  {path: '', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'main', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'main/:id', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: '?token', component: ResetPasswordComponent},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes), CommonModule],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
