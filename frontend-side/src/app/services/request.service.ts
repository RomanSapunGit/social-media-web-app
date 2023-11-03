import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {AuthService} from "./auth.service";
import {TokenModel} from "../model/token.model";
import {environment} from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class RequestService {
    private baseUrl = environment.backendUrl;
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
                    this.router.navigate(['/main'])
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

    getPosts(page: number, token: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if(pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
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

    getUsers(page: number, token: string | null, pageSize?: number) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user`, {params, headers});
    }

    getTags(page: number, token: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/tag`, {params, headers});
    }

    getPostsByTag(page: number, token: string | null, tag: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/tag/${tag}`, {params, headers});
    }

    getPostsByUsername(page: number, token: string | null, username: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if(pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/author/${username}`, {params, headers});
    }

    isUserHasSubscriptions(token: string | null) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user/follower`, {headers})
    }

    getPostsBySubscription(token: string | null, page: number, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortByValue', sortByValue);
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

    searchPostsByText(token: string | null, text: string, page: number, pageSize: number, sortBy: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('page', page.toString());
        params = params.set('pageSize', pageSize.toString());
        params = params.set('sortBy', sortBy);
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

    sendNewSubscriptionNotification(token: string | null, username: string, message: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/notifications/subscriptions`, {username, message}, {headers})
    }

    getNotifications(token: string, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/notifications/${username}`, {headers})
    }

    sendNotificationToSlack(message: string, causedBy: string, timestamp: Date) {
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

    getTagsByText(token: string | null, text: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/tag/${text}`, {headers})
    }

    getUsersByText(token: string | null, text: string, page: number) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('page', page.toString());
        return this.http.get(`${this.baseUrl}/api/v1/user/${text}`, {headers, params})
    }

    completeNotificationSSE(token: string, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('username', username.toString());
        return this.http.delete(`${this.baseUrl}/sse/notifications/complete`, {headers, params})
    }

    completePostUpdateSSE(token: string, postId: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('postId', postId);
        return this.http.delete(`${this.baseUrl}/sse/posts/updates/complete`, {headers, params})
    }

    translateText(text: string, token: string, targetLanguage: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        let params = new HttpParams();
        params = params.set('text', text);
        params = params.set('targetLanguage', targetLanguage);
        return this.http.put(`${this.baseUrl}/api/v1/translations`, {}, {headers, params});
    }

    addUpvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/post/upvote`, {identifier}, {headers})
    }

    removeUpvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/post/upvote/${identifier}`, {headers})
    }

    addDownvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/post/downvote`, {identifier}, {headers})
    }

    removeDownvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/post/downvote/${identifier}`, {headers})
    }

    isPostUpvoted(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/upvote/${identifier}`, {headers})
    }

    isPostDownvoted(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/downvote/${identifier}`, {headers})
    }
}
