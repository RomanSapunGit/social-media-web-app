import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from "./component/register/register.component";
import {LoginComponent} from "./component/login/login.component";
import {AuthGuard} from "./provider/auth.guard";
import {MainPageComponent} from "./component/mainpage/mainpage.component";
import {PageNotFoundComponent} from "./component/pagenotfound/pagenotfound.component";
import {ResetPasswordComponent} from "./component/reset-password/reset-password.component";
import {RegisterViaGoogleComponent} from "./component/register-via-google/register-via-google.component";

const routes: Routes = [
  {path: '', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'main', component: MainPageComponent, canActivate: [AuthGuard]},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: '?token', component: ResetPasswordComponent},
  {path: 'register/google', component: RegisterViaGoogleComponent},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
