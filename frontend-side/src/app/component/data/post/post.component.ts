import {ChangeDetectorRef, Component, HostListener, Input} from '@angular/core';
import {Page} from "../../../model/page.model";
import {
  BehaviorSubject, concatMap,
  distinctUntilChanged,
  forkJoin,
  map,
  Observable,
  of, ReplaySubject, share, shareReplay, Subscription,
  switchMap,
  take,
  tap,
  Timestamp
} from "rxjs";
import {PostServiceService} from "../../../service/post-service.service";
import {MatDialogService} from "../../../service/mat-dialog.service";
import {AuthService} from "../../../service/auth.service";
import {PostActionService} from "../../../service/post-action.service";
import {PostModel} from "../../../model/post.model";
import {RoutingService} from "../../../service/routing.service";

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss']
})
export class PostComponent {
  posts: ReplaySubject<Page>;
  currentPostPage: BehaviorSubject<number>;
  isPostPaginationVisible$: Observable<boolean>;
  isUserSubscribed: BehaviorSubject<boolean>;
  isLoading: BehaviorSubject<boolean>;
  subscriptions: Subscription;
  currentUser: string | null;
  errorMessage: string;
  @Input() tagName: string | null;
  @Input() username: string | null;
  loadedPosts: PostModel[] = [];
  totalPages: BehaviorSubject<number>;

  constructor(private postService: PostServiceService, private matDialogService: MatDialogService,
              private authService: AuthService, private postActionService: PostActionService,
              private routingService: RoutingService) {
    this.totalPages = new  BehaviorSubject(0);
    this.posts = new ReplaySubject<Page>(1);
    this.currentPostPage = new BehaviorSubject<number>(0);
    this.isPostPaginationVisible$ = new Observable<boolean>();
    this.tagName = '';
    this.username = '';
    this.isUserSubscribed = new BehaviorSubject<boolean>(false);
    this.isLoading = new BehaviorSubject<boolean>(true);
    this.subscriptions = new Subscription();
    this.currentUser = this.authService.getUsername();
    this.errorMessage = '';
  }

  ngOnInit() {
    this.postActionService.postCreated$.subscribe({
      next: () => {
      }
    });

    this.postService.textFound$.subscribe({
      next: (text) => {
        this.isLoading.next(true);
        if (!text) {
          this.fetchPosts().pipe(
            tap((page) => {
              this.posts.next(page);
            })
          ).subscribe();
        } else {
          this.postService.searchPostsByText(text, 0).pipe(
            tap((page) => {
              this.posts.next(page);
            })
          ).subscribe();
          this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
          this.isLoading.next(false);
        }
      }
    })

    const subscription = this.postService.isUserHasSubscriptions().pipe(
      concatMap(isUserHasSubscriptions => {
        this.isUserSubscribed.next(!this.username && !this.tagName ? isUserHasSubscriptions : false);
        return this.fetchPosts();
      })).subscribe({
      next: (page) => {
        this.loadedPosts = [...this.loadedPosts, ...page.entities];
        const pageData = new Page(this.loadedPosts, page.total, page.currentPage, page.totalPages);
        this.posts.next(pageData);
        this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
        this.isLoading.next(false);
        shareReplay(1)
      },
      error: (error: any) => this.errorMessage = 'Something went wrong:' + error.error.message,
    });
    this.subscriptions.add(subscription);
  }

  hasPostsBySubscription(): Observable<boolean> {
    return this.posts.pipe(
      map(postsData => {
        if (postsData.total == 0 && this.isUserSubscribed.getValue()) {
          this.onPostChange();
          return false;
        }
        return true;
      })
    );
  }

  nextPostPage() {
    if (!this.isLoading.getValue()) {
      this.currentPostPage.pipe(take(1)).subscribe((currentPage) => {
        take(1);
        this.isLoading.next(true);
        this.currentPostPage.next(currentPage + 1);
        this.fetchPosts().pipe(
          tap((page) => {
            this.loadedPosts = [...this.loadedPosts, ...page.entities];
            const pageData = new Page(this.loadedPosts, page.total, currentPage, page.totalPages);
            this.posts.next(pageData);
            this.isLoading.next(false);
          }),
        ).subscribe();
      });
    }
  }

  onPostChange() {
    window.scrollTo(0, 0);
    this.isLoading.next(true);
    this.isUserSubscribed.next(!this.isUserSubscribed.getValue());
    this.currentPostPage.next(0);
    this.fetchPosts().subscribe(posts => {
      this.posts.next(posts);
      this.isLoading.next(false);
    });
    this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
  }

  fetchPosts(): Observable<Page> {
    return this.currentPostPage.pipe(
      take(1),
      distinctUntilChanged(),
      switchMap((currentPage) =>
        this.isUserSubscribed.getValue()
          ? this.postService.fetchPostsBySubscription(currentPage)
          : this.postService.fetchPostsByPage(currentPage, this.tagName, this.username)))
  }

  updatePost(postIdentifier: string) {
    this.matDialogService.updatePost(postIdentifier);
  }

  isPostPaginationVisible(posts: Observable<Page>): Observable<boolean> {
    return this.postService.isPostPaginationVisible(posts);
  }

  displaySinglePost(postIdentifier: string) {
    this.routingService.setPathVariable(postIdentifier);
    return this.matDialogService.displaySinglePost(postIdentifier);
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }
}
