import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {
  AsyncSubject,
  BehaviorSubject,
  delay,
  first,
  Observable,
  ReplaySubject,
  shareReplay,
  Subscription,
  take, tap
} from "rxjs";
import {Page} from "../../../model/page.model";
import {CommentService} from "../../../service/comment.service";
import {MatDialogService} from "../../../service/mat-dialog.service";
import {AuthService} from "../../../service/auth.service";
import {CacheCommentService} from "../../../service/comment-cache.service";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent {
  @Input() postIdentifier: string;
  postComments: ReplaySubject<Page>;
  @Input() currentCommentPage: { [postId: string]: number };
  isCommentPaginationVisible$: Observable<boolean>;
  @Input() commentVisibility: boolean;
  currentUser: string | null;
  subscription: Subscription;

  constructor(private commentService: CommentService, private matDialogService: MatDialogService,
              private changeDetectorRef: ChangeDetectorRef, private authService: AuthService,
              private cacheCommentService: CacheCommentService) {
    this.postIdentifier = '';
    this.postComments = new ReplaySubject<Page>(1);
    this.currentCommentPage = {};
    this.isCommentPaginationVisible$ = new Observable<boolean>();
    this.commentVisibility = false;
    this.currentUser = this.authService.getUsername();
    this.subscription = new Subscription();
  }

  async ngOnInit() {
    this.currentCommentPage[this.postIdentifier] = 0;
    this.getCachedComments(this.postIdentifier);
    this.commentService.commentCreated$.subscribe(() => {
      if (this.postIdentifier) {
        this.fetchComments(this.postIdentifier);
      }
    });
  }

  fetchComments(postId: string): void {
    const sub = this.commentService.getComments(postId, this.currentCommentPage[postId]).pipe(take(1))
      .subscribe((commentPage: Page) => {
        console.log(commentPage.entities)
        this.postComments.next(commentPage);
        console.log(this.postComments)
        this.cacheCommentService.save(postId, this.currentCommentPage[postId], commentPage);
      });
    this.subscription.add(sub);
    this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
  }

  getCachedComments(postId: string): void {
    console.log('getCachedComments');

    this.cacheCommentService.load(postId, this.currentCommentPage[postId])
      .pipe(
        tap((commentPage) => {
          if (commentPage) {
            console.log('Cached comments found');
            this.postComments.next(commentPage);
            this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
            return;
          } else {
            console.log('No cached comments found, fetching...');
            this.fetchComments(postId);
            return;
          }
        })
      ).subscribe();
  }

  previousCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId];
    if (currentPage > 0) {
      this.currentCommentPage[postId] = currentPage - 1;
      this.getCachedComments(postId);
    }
  }

  async nextCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId] || 0;
    const postComments = this.postComments;
    if (postComments) {
      const sub = postComments.pipe(take(1)).subscribe(async (page: Page) => {
        const totalPages = page?.totalPages || 0;
        if (currentPage < totalPages - 1) {
          console.log('check')
          this.currentCommentPage[postId] = currentPage + 1;
          await this.getCachedComments(postId);
        }
      });
      this.subscription.add(sub);
    }
  }

  createComment(id: string) {
    this.matDialogService.createComment(id);
  }

  updateComment(commentId: string, commentTitle: string, commentDescription: string) {
    this.matDialogService.updateComment(commentId, commentTitle, commentDescription);
  }

  isCommentPaginationVisible(postComments: Observable<Page>): Observable<boolean> {
    return this.commentService.isCommentPaginationVisible(postComments);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.postComments = new ReplaySubject<Page>(1);
  }
}
