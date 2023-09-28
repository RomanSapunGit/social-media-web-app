import {Component, Input} from '@angular/core';
import {Page} from "../../../model/page.model";
import {tap} from "rxjs"
import {
    BehaviorSubject, concatMap,
    distinctUntilChanged,
    map,
    Observable, ReplaySubject, shareReplay, Subscription,
    switchMap,
    take,
} from "rxjs";
import {PostService} from "../../../service/post.service";
import {MatDialogService} from "../../../service/mat-dialog.service";
import {AuthService} from "../../../service/auth.service";
import {PostActionService} from "../../../service/post-action.service";
import {PostModel} from "../../../model/post.model";
import {RoutingService} from "../../../service/routing.service";
import {SearchByTextService} from "../../../service/search-by-text.service";

@Component({
    selector: 'app-post',
    templateUrl: './posts.component.html',
    styleUrls: ['./posts.component.scss']
})
export class PostsComponent {
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

    constructor(private postService: PostService, private matDialogService: MatDialogService,
                private authService: AuthService, private postActionService: PostActionService,
                private routingService: RoutingService, private searchByTextService: SearchByTextService) {
        this.totalPages = new BehaviorSubject(0);
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
            next: (post: PostModel) => {
                this.updatePostView(post);
            }
        });
        if(!(this.username || this.tagName)) {
            this.searchByTextService.textFound$.subscribe({
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
                                this.loadedPosts = page.entities;
                                this.posts.next(page);
                            })
                        ).subscribe();
                        this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
                        this.isLoading.next(false);
                    }
                }
            })
        }
        const subscription = this.postService.isUserHasSubscriptions().pipe(
            concatMap((isUserHasSubscriptions: boolean) => {
                this.isUserSubscribed.next(!this.username && !this.tagName ? isUserHasSubscriptions : false);
                return this.fetchPosts();
            })).subscribe({
            next: (page) => {
                this.loadedPosts = [...this.loadedPosts, ...page.entities];
                const pageData = new Page(this.loadedPosts, page.total, page.currentPage, page.totalPages);
                this.posts.next(pageData);
                console.log(this.loadedPosts)
                this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
                this.isLoading.next(false);
                shareReplay(1)
            },
            error: (error: any) => {
                this.errorMessage = 'Something went wrong:' + error.error.message
            },
        });
        this.subscriptions.add(subscription);
    }

    hasPostsBySubscription(): Observable<boolean> {
        return this.posts.pipe(
            take(1),
            map(postsData => {
                if (postsData.entities.length == 0 && this.isUserSubscribed.getValue()) {
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
            this.loadedPosts = posts.entities;
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

    updatePost(postIdentifier: string, title: string, description: string) {
        this.matDialogService.updatePost(postIdentifier, title, description);
    }

    updatePostView(updatedPost: PostModel) {
        if (updatedPost) {
            const postIndex = this.loadedPosts.findIndex((post) => post.identifier === updatedPost.identifier);
            if (postIndex !== -1) {
                this.loadedPosts[postIndex] = updatedPost;
            }
            const updatedPageData = new Page([...this.loadedPosts], this.totalPages.getValue(),
                this.currentPostPage.getValue(), this.totalPages.getValue());
            this.posts.next(updatedPageData);
        }
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
