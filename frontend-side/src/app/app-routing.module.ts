import {NgModule} from '@angular/core';
import {NoPreloading, PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from "./component/register/register.component";
import {LoginComponent} from "./component/login/login.component";
import {AuthGuard} from "./provider/auth.guard";
import {MainPageComponent} from "./component/main-page/main-page.component";
import {PageNotFoundComponent} from "./component/page-not-found/pagenotfound.component";
import {ResetPasswordComponent} from "./component/reset-password/reset-password.component";
import {UsersDataResolver} from "./resolver/user-data-resolver";
import {PostsDataResolver} from "./resolver/posts-data-resolver";
import {TagsDataResolver} from "./resolver/tags-data-resolver";

const routes: Routes = [
    {path: '', component: RegisterComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {
        path: 'main/users',
        component: MainPageComponent,
        canActivate: [AuthGuard],
        resolve: {
            userData: UsersDataResolver
        }
    },
    {
        path: 'main/posts',
        component: MainPageComponent,
        canActivate: [AuthGuard],
        resolve: {
            postData: PostsDataResolver
        }
    },
    {
        path: 'main/tags',
        component: MainPageComponent,
        canActivate: [AuthGuard],
        resolve: {
            tagsData: TagsDataResolver
        }
    },
    {path: 'main/post/:id', component: MainPageComponent, canActivate: [AuthGuard]},
    {
        path: 'main',
        component: MainPageComponent,
        canActivate: [AuthGuard], resolve: {
            postData: PostsDataResolver,
            tagsData: TagsDataResolver,
            userData: UsersDataResolver
        }
    },
    {path: 'reset-password', component: ResetPasswordComponent},
    {path: '?token', component: ResetPasswordComponent},
    {path: '**', component: PageNotFoundComponent},
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {preloadingStrategy: NoPreloading}),
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
