import {Injectable} from '@angular/core';
import {Page} from "../model/page.model";
import {map, Observable, of, ReplaySubject, take} from "rxjs";
import {RequestService} from "./request.service";
import {AuthService} from "./auth.service";
import {PostModel} from "../model/post.model";
import {PostViewModel} from "../model/post-view.model";
import {UserModel} from "../model/user.model";
import {ValidatorModel} from "../model/validator.model";
import {HttpParams} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class PostService {

    constructor(private authService: AuthService,
                private requestService: RequestService) {
    }

    addPostToSavedList(identifier: string) {
       return this.requestService.addPostToSavedList(identifier);
    }
    deletePostFromSavedList(identifier: string) {
        return this.requestService.deletePostFromSavedList(identifier);
    }
    findPostInSavedList(identifier: string) {
        return this.requestService.findPostInSavedList(identifier).pipe(take(1),map(isFound => isFound as boolean))
    }

    fetchPostsByPage(page: number, tagName: string | null, username: string | null, pageSize?: number, sortByValue?: string): Observable<Page> {
        const token = this.authService.getAuthToken();
        let fetchPostsObservable: Observable<any>;

        if (tagName) {
            const codedTagName = tagName.replace(/#/, '%23');
            fetchPostsObservable = this.requestService.getPostsByTag(page, token, codedTagName, pageSize, sortByValue);
        } else if (username) {
            fetchPostsObservable = this.requestService.getPostsByUsername(page, token, username, pageSize, sortByValue);
        } else {
            fetchPostsObservable = this.requestService.getPosts(page, token, pageSize, sortByValue);
        }
        return fetchPostsObservable.pipe(
            map(response => this.convertToPostPage(response))
        );
    }

    deletePost(postId: string): Observable<any> {
        let token = this.authService.getAuthToken();
        return this.requestService.deletePost(token, postId);
    }

    fetchPostsBySubscription(page: number, pageSize?: number, sortByValue?: string): Observable<Page> {
        const token = this.authService.getAuthToken();
        return this.requestService.getPostsBySubscription(token, page, pageSize, sortByValue).pipe(
            map(response => this.convertToPostPage(response))
        );
    }

    isUserHasSubscriptions(username: string | null, tag: string | null): Observable<boolean> {
        if(username || tag) {
            return of(false);
        }
        return this.requestService.isUserHasSubscriptions().pipe(map((response: any) => {
            return response.valid;
        }));
    }

    isPostPaginationVisible(posts: Observable<Page>): Observable<boolean> {
        return posts.pipe(
            map(posts => posts?.totalPages !== undefined && posts?.totalPages !== 0 && posts?.totalPages !== 1)
        );
    }

    searchPostsByText(text: string, page: number, pageSize: number, sortBy: string): Observable<Page> {
        return this.requestService.searchPostsByText( text, page, pageSize, sortBy).pipe(
            map(response => this.convertToPostPage(response))
        );
    }

    getSavedPosts(page: number, pageSize: number, sortBy: string) {
        return this.requestService.getSavedPosts(page, pageSize, sortBy).pipe(
            map(response => this.convertToPostPage(response))
        )
    }

    getPostById(identifier: string): Observable<PostModel> {
        let token = this.authService.getAuthToken();
        return this.requestService.getPostById(token, identifier).pipe(
            map(response => response as PostModel)
        )
    }

    addUpvote(identifier: string): Observable<number> {
        let token = this.authService.getAuthToken();
        return this.requestService.addUpvote(token, identifier).pipe(
            map(response => response as number)
        )
    }

    removeUpvote(identifier: string): Observable<number> {
        let token = this.authService.getAuthToken();
        return this.requestService.removeUpvote(token, identifier).pipe(
            map(response => response as number)
        )
    }

    addDownvote(identifier: string): Observable<number> {
        let token = this.authService.getAuthToken();
        return this.requestService.addDownvote(token, identifier).pipe(
            map(response => response as number)
        )
    }

    removeDownvote(identifier: string): Observable<number> {
        let token = this.authService.getAuthToken();
        return this.requestService.removeDownvote(token, identifier).pipe(
            map(response => response as number)
        )
    }

    isUpvoteMade(identifier: string): Observable<boolean> {
        let token = this.authService.getAuthToken();
        return this.requestService.isPostUpvoted(token, identifier).pipe(map((response: any) => {
            return response.valid;
        }))
    }

    isDownvoteMade(identifier: string): Observable<boolean> {
        let token = this.authService.getAuthToken();
        return this.requestService.isPostDownvoted(token, identifier).pipe(map((response: any) => {
            return response.valid;
        }))
    }

    private convertToPostPage(response: any): Page {
        return new Page(
            response.entities as PostModel[],
            response.total as number,
            response.currentPage as number,
            response.totalPages as number
        );
    }
}
