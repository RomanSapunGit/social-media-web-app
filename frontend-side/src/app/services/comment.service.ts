import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, map, Observable, of, ReplaySubject, Subject} from "rxjs";
import {CommentModel} from "../model/comment.model";
import {Page} from "../model/page.model";
import {AuthService} from "./auth.service";
import {RequestService} from "./request.service";

@Injectable({
    providedIn: 'root'
})
export class CommentService {
    private commentCreated: ReplaySubject<CommentModel> = new ReplaySubject<CommentModel>();
    private currentCommentPage: number;

    clearSubject() {
        this.commentCreated = new ReplaySubject<CommentModel>();
    }

    get commentCreated$() {
        return this.commentCreated;
    }

    addComment(commentModel: CommentModel) {
        this.commentCreated.next(commentModel);
    }

    constructor(private authService: AuthService,
                private requestService: RequestService) {
        this.currentCommentPage = 0;
    }

    getComments(postId: string, currentCommentPage: number): Observable<Page> {
        const currentPage = currentCommentPage || 0;
        let token = this.authService.getAuthToken();
        return this.requestService.getCommentsByPost(postId, token, currentPage).pipe(
            map((response: any) => {
                return new Page(response.entities, response.total, response.currentPage, response.totalPages);
            }),
            catchError(() => {
                return of(undefined);
            })
        ) as Observable<Page>;
    }

    isCommentPaginationVisible(postComments: Observable<Page>): Observable<boolean> {
        if (postComments) {
            return postComments.pipe(
                map((comments) => comments?.totalPages !== undefined && comments?.totalPages !== 0 && comments?.totalPages !== 1)
            );
        } else {
            return of(false);
        }
    }
}
