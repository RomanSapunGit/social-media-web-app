import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {AuthService} from "./auth.service";
import {TokenModel} from "../model/token.model";
import {ServerSendEventService} from "./server-send-event.service";

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
        return this.http.post(`${this.baseUrl}/api/v1/token`, loginData);
    }

    loginAndRedirect(loginData: { username: string, password: string }): void {
        this.login(loginData).subscribe(
            {
                next: (response: any) => {
                    const token: string = (response as TokenModel).token;
                    this.authService.setAuthData(token, loginData.username);
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

    register(registerData: FormData) {
        return this.http.post(`${this.baseUrl}/api/v1/account`, registerData);
    }

    loginViaGoogle(token: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/token/account`, null, {headers});
    }

    loginViaGoogleAndRedirect(token: string): void {
        this.loginViaGoogle(token).subscribe(
            {
                next: (response: any) => {
                    const token: string = (response as TokenModel).token;
                    console.log(((response as TokenModel).username))
                    this.authService.setAuthData(token, (response as TokenModel).username);
                },
                error: (error: any) =>
                    console.log('Error during login ' + error.error.message),
                complete: () =>
                    this.router.navigate(['/main']).then(() => {
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
        return this.http.get<boolean>(`${this.baseUrl}/api/v1/token/${username}`, {headers});
    }

    refreshToken(token: string | null, username: string | null): void {
        this.http.put<string>(`${this.baseUrl}/api/v1/token`, {token, username}).subscribe(
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
        return this.http.post<void>(`${this.baseUrl}/api/v1/account/${email}`, {});
    }

    resetPassword(token: string, password: string, matchPassword: string) {
        return this.http.put<void>(`${this.baseUrl}/api/v1/account/${token}`, {password, matchPassword});
    }

    getPosts(page: number, token: string | null) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/search`, {params, headers});
    }

    getCommentsByPost(id: string, token: string | null, page: number) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/comment/${id}`, {params, headers});
    }

    createComment(postIdentifier: string | null, token: string | null, creationData: { title: string; description: string }) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/comment`, {postIdentifier, ...creationData}, {headers});
    }

    updateComment(updateData: { title: string; description: string }, token: string | null, id: string | null) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.patch(`${this.baseUrl}/api/v1/comment/${id}`, {...updateData}, {headers})
    }

    getUsers(page: number, token: string | null) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user`, {params, headers});
    }

    getTags(page: number, token: string | null) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/tag`, {params, headers});
    }

    getPostsByTag(page: number, token: string | null, tag: string | null) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/tag/${tag}`, {params, headers});
    }

    getPostsByUsername(page: number, token: string | null, username: string | null) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/author/${username}`, {params, headers});
    }

    isUserHasSubscriptions(token: string | null) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user/follower`, {headers})
    }

    getPostsBySubscription(token: string | null, page: number) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        return this.http.get(`${this.baseUrl}/api/v1/post/follower`, {params, headers})
    }

    getImageByUser(token: string | null) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        return this.http.get(`${this.baseUrl}/api/v1/image`, {headers})
    }

    createPost(token: string | null, postData: FormData) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        return this.http.post(`${this.baseUrl}/api/v1/post`, postData, {headers})
    }

    updatePost(token: string | null, postData: FormData) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        return this.http.put(`${this.baseUrl}/api/v1/post`, postData, {headers})
    }

    searchPostsByText(token: string | null, text: string, page: number) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('page', page.toString());

        return this.http.get(`${this.baseUrl}/api/v1/post/search/${text}`, {params, headers})
    }

    getPostById(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/${identifier}`, {headers})
    }

    sendCommentNotification(token: string, commentIdentifier: string, message: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/notifications/comments`, {commentIdentifier, message}, {headers})
    }

    getNotifications(token: string, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/notifications/${username}`, {headers})
    }

    sendNotificationToSlack( message: string, causedBy: string, timestamp: Date) {
        return this.http.post(`${this.baseUrl}/api/v1/notifications/slack`, {causedBy, timestamp, message})
    }

    addFollowing(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/user/follower`, {username}, {headers})
    }

    removeFollowing(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/user/follower/${username}`, {headers})
    }

    findFollowingByUsername(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user/follower/${username}`, {headers})
    }
}
