import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, map, Observable, of, Subject} from "rxjs";
import {CommentModel} from "../model/comment.model";
import {Page} from "../model/page.model";
import {AuthService} from "./auth.service";
import {RequestService} from "./request.service";

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private commentCreated: Subject<any> = new Subject<any>();
  private currentCommentPage: number;

  get commentCreated$() {
    return this.commentCreated;
  }

  addComment(commentModel: CommentModel) {
    this.commentCreated.next(commentModel);
  }

  constructor(private authService: AuthService,
              private requestService: RequestService,
              private page: Page
  ) {
    this.currentCommentPage = 0;
  }

  getComments(postId: string, currentCommentPage:  number ): Observable<Page> {
    const currentPage = currentCommentPage || 0;
    let token = this.authService.getAuthToken();
    return this.requestService.getCommentsByPost(postId, token, currentPage).pipe(
      map((response: any) => {
        return this.page.create(
          response['entities'],
          response['total-items'],
          response['current-page'],
          response['total-pages']
        );
      }),
      catchError(() => {
        return of(undefined);
      })
    ) as Observable<Page>;
  }

  isCommentPaginationVisible(postComments: Observable<Page>): Observable<boolean> {
    if (postComments) {
      return postComments.pipe(
        map((comments) => comments?.pages !== undefined && comments?.pages !== 0 && comments?.pages !== 1)
      );
    } else {
      return of(false);
    }
  }
}
