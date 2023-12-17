import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {AuthService} from "./auth.service";
import {TokenModel} from "../model/token.model";
import {environment} from "../../environments/environment";
import {NotificationService} from "./notification.service";
import {RequestUpdatePostModel} from "../model/request-update-post.model";

@Injectable({
    providedIn: 'root'
})
export class RequestService {
    private baseUrl = environment.backendUrl;
    private csrfToken: any;

    constructor(private http: HttpClient, private router: Router, private authService: AuthService) {
        this.csrfToken = null;
    }

    login(loginData: { username: string, password: string }) {
        return this.http.post(`${this.baseUrl}/api/v1/account/login`, loginData,
            {withCredentials: true, headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})});
    }

    logout() {
        return this.http.delete(`${this.baseUrl}/api/v1/account/logout`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        });
    }

    register(registerData: FormData) {
        let headers = new HttpHeaders()
        headers.set('X-CSRF-TOKEN', this.csrfToken)
        return this.http.post(`${this.baseUrl}/api/v1/account`, registerData, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        });
    }

    validateSession() {
        return this.http.get<boolean>(`${this.baseUrl}/api/v1/account`, {withCredentials: true});
    }


    loginViaGoogle(token: string) {
        console.log(token)
        console.log(this.csrfToken.token)
        return this.http.post(`${this.baseUrl}/api/v1/account/google/login`, null, {
            withCredentials: true,
            headers: new HttpHeaders({
                'X-CSRF-TOKEN': this.csrfToken.token as string,
                'Authorization': `Bearer ${token}`
            })
        });
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
        return this.http.post<void>(`${this.baseUrl}/api/v1/account/${email}`, {}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        });
    }

    resetPassword(token: string, password: string, matchPassword: string) {
        return this.http.put<void>(`${this.baseUrl}/api/v1/account/${token}`, {password, matchPassword}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        });
    }

    getPosts(page: number, token: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        headers.set('X-CSRF-TOKEN', this.csrfToken);
        return this.http.get(`${this.baseUrl}/api/v1/post/search`, {withCredentials: true, params, headers});
    }

    getCommentsByPost(postId: string, token: string | null, pageNumber: number) {
        let params = new HttpParams();
        params = params.set('pageNumber', pageNumber.toString());
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/comment/${postId}`, {withCredentials: true, params, headers});
    }

    createComment(postIdentifier: string | null, token: string | null, creationData: {
        title: string;
        description: string
    }) {
        return this.http.post(`${this.baseUrl}/api/v1/comment`, {postIdentifier, ...creationData}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        });
    }

    updateComment(updateData: { title: string; description: string }, token: string | null, id: string | null) {
        return this.http.patch(`${this.baseUrl}/api/v1/comment/${id}`, {...updateData}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    getUsers(page: number, pageSize?: number) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        return this.http.get(`${this.baseUrl}/api/v1/user`, {withCredentials: true, params});
    }

    getTags(page: number, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        return this.http.get(`${this.baseUrl}/api/v1/tag`, {withCredentials: true, params});
    }

    getPostsByTag(page: number, token: string | null, tag: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/tag/${tag}`, {withCredentials: true, params, headers});
    }

    getPostsByUsername(page: number, token: string | null, username: string | null, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortBy', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/author/${username}`, {
            withCredentials: true,
            params,
            headers
        });
    }

    isUserHasSubscriptions() {
        return this.http.get(`${this.baseUrl}/api/v1/user/follower`, {withCredentials: true})
    }

    getPostsBySubscription(token: string | null, page: number, pageSize?: number, sortByValue?: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        if (pageSize)
            params = params.set('pageSize', pageSize.toString());
        if (sortByValue)
            params = params.set('sortByValue', sortByValue);
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/follower`, {withCredentials: true, params, headers})
    }

    getImageByUser(token: string | null) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

        return this.http.get(`${this.baseUrl}/api/v1/image`, {withCredentials: true, headers})
    }

    createPost(token: string | null, postData: FormData) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`)
            .set('X-CSRF-TOKEN', this.csrfToken.token);

        return this.http.post(`${this.baseUrl}/api/v1/post`, postData, {withCredentials: true, headers})
    }

    updatePost(postUpdateData: RequestUpdatePostModel) {
        console.log(postUpdateData)
        return this.http.put(`${this.baseUrl}/api/v1/post`, postUpdateData, {
            withCredentials: true,
            headers: new HttpHeaders({
                'X-CSRF-TOKEN': this.csrfToken.token as string,
            })
        })
    }

    searchPostsByText(text: string, page: number, pageSize: number, sortBy: string) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        params = params.set('pageSize', pageSize.toString());
        params = params.set('sortBy', sortBy);
        return this.http.get(`${this.baseUrl}/api/v1/post/search/${text}`, {withCredentials: true, params})
    }

    getPostById(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/${identifier}`, {withCredentials: true, headers})
    }

    sendCommentNotification(commentIdentifier: string, message: string) {
        return this.http.post(`${this.baseUrl}/api/v1/notifications/comments`, {commentIdentifier, message}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    sendNewSubscriptionNotification(token: string | null, username: string, message: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/notifications/subscriptions`, {username, message}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    getNotifications(username: string) {
        return this.http.get(`${this.baseUrl}/api/v1/notifications/${username}`, {withCredentials: true})
    }

    sendNotificationToSlack(message: string, causedBy: string, timestamp: Date) {
        console.log(this.csrfToken.token + 'p')
        return this.http.post(`${this.baseUrl}/api/v1/notifications/slack`, {causedBy, timestamp, message}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    addFollowing(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/user/follower`, {username}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    removeFollowing(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/user/follower/${username}`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    findFollowingByUsername(token: string | null, username: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/user/follower/${username}`, {withCredentials: true, headers})
    }

    getTagsByText(token: string | null, text: string, page: number, pageSize: number) {
        let params = new HttpParams();
        console.log(page, pageSize);
        params = params.set('page', page.toString());
        params = params.set('pageSize', pageSize.toString());
        let headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        console.log('check');
        return this.http.get(`${this.baseUrl}/api/v1/tag/${text}`, {withCredentials: true, params, headers});
    }

    getUsersByText(text: string, page: number, pageSize: number) {
        let params = new HttpParams();
        params = params.set('page', page.toString());
        params = params.set('pageSize', pageSize.toString());
        return this.http.get(`${this.baseUrl}/api/v1/user/${text}`, {withCredentials: true, params})
    }

    completeNotificationSSE(username: string) {
        let params = new HttpParams();
        params = params.set('username', username.toString());
        return this.http.delete(`${this.baseUrl}/sse/notifications/complete`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string}), params
        })
    }

    completePostUpdateSSE(postId: string) {
        let params = new HttpParams();
        params = params.set('postId', postId);
        return this.http.delete(`${this.baseUrl}/sse/posts/updates/complete`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string}), params
        })
    }

    translateText(text: string, targetLanguage: string) {
        let params = new HttpParams();
        params = params.set('text', text);
        params = params.set('targetLanguage', targetLanguage);
        return this.http.put(`${this.baseUrl}/api/v1/translations`, {}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string}), params
        });
    }

    addUpvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/post/upvote`, {identifier}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    removeUpvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/post/upvote/${identifier}`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    addDownvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.post(`${this.baseUrl}/api/v1/post/downvote`, {identifier}, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    removeDownvote(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/post/downvote/${identifier}`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    isPostUpvoted(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/upvote/${identifier}`, {withCredentials: true, headers})
    }

    isPostDownvoted(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.get(`${this.baseUrl}/api/v1/post/downvote/${identifier}`, {withCredentials: true, headers})
    }

    deletePost(token: string | null, identifier: string) {
        const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
        return this.http.delete(`${this.baseUrl}/api/v1/post/${identifier}`, {
            withCredentials: true,
            headers: new HttpHeaders({'X-CSRF-TOKEN': this.csrfToken.token as string})
        })
    }

    getCsrf() {
        this.http.get(`${this.baseUrl}/api/v1/csrf/token`, {withCredentials: true}).subscribe(
            (token: any) => this.csrfToken = token);
    }
}
