import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {Page} from "../../../model/page.model";
import {of, tap} from "rxjs"
import {
    BehaviorSubject, concatMap,
    distinctUntilChanged,
    map,
    Observable, ReplaySubject, shareReplay, Subscription,
    switchMap,
    take,
} from "rxjs";
import {PostService} from "../../../services/post.service";
import {MatDialogService} from "../../../services/mat-dialog.service";
import {AuthService} from "../../../services/auth.service";
import {PostActionService} from "../../../services/post-action.service";
import {PostModel} from "../../../model/post.model";
import {RoutingService} from "../../../services/routing.service";
import {SearchByTextService} from "../../../services/search-by-text.service";
import {ActivatedRoute} from "@angular/router";
import {FileDTO} from "../../../model/file.model";

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
    @Input() displaySavedComments: boolean;
    loadedPosts: PostModel[] = [];
    totalPages: BehaviorSubject<number>;
    pageSize!: number;
    sortBy!: string;
    searchText: string;
    isDownVoteMade: boolean;
    isUpvoteMade: boolean;

    constructor(private postService: PostService, private matDialogService: MatDialogService,
                private authService: AuthService, private postActionService: PostActionService,
                private routingService: RoutingService, private searchByTextService: SearchByTextService,
                private route: ActivatedRoute, private changeDetectorRef: ChangeDetectorRef) {
        this.totalPages = new BehaviorSubject(0);
        this.posts = new ReplaySubject<Page>(1);
        this.currentPostPage = new BehaviorSubject<number>(0);
        this.isPostPaginationVisible$ = new Observable<boolean>();
        this.tagName = '';
        this.username = '';
        this.isUserSubscribed = new BehaviorSubject<boolean>(false);
        this.isLoading = new BehaviorSubject<boolean>(true);
        this.subscriptions = new Subscription();
        this.currentUser = localStorage.getItem('username');
        this.errorMessage = '';
        this.searchText = '';
        this.isDownVoteMade = false;
        this.isUpvoteMade = false;
        this.displaySavedComments = false;
    }


    ngOnInit() {
        this.postActionService.deletePost$()
            .subscribe(post => {
                this.updatePostViewAfterDeletion(post);
            });
        this.route.queryParams.subscribe(params => {
            if (this.pageSize && this.sortBy) {
                this.pageSize = params['pageSize'] || 20;
                this.sortBy = params['sortBy'] || 'creationTime';
                this.currentPostPage.next(0);
                this.fetchPosts().pipe(
                    shareReplay(1),
                    take(1),
                    tap(page => this.posts.next(page))
                ).subscribe();
            }
            this.pageSize = params['pageSize'] || 20;
            this.sortBy = params['sortBy'] || 'creationTime';
        })
        this.postActionService.postCreated$.subscribe({
            next: (post: PostModel) => {
                this.updatePostView(post);
                this.changeDetectorRef.detectChanges();
            }
        });

        if (!(this.username || this.tagName)) {
            this.searchByTextService.textFound$.subscribe({
                next: (text) => {
                    this.isLoading.next(true);
                    this.currentPostPage.next(0);
                    if (!text) {
                        this.searchText = '';
                        this.fetchPosts().pipe(
                            take(1),
                            shareReplay(),
                            tap((page) => {
                                this.posts.next(page);
                            })
                        ).subscribe();
                    } else {
                        this.searchText = text;
                        this.postService.searchPostsByText(text, 0, this.pageSize, this.sortBy).pipe(
                            take(1),
                            tap((page) => {
                                this.loadedPosts = page.entities;
                                this.posts.next(page);
                            })
                        ).subscribe();
                    }
                    this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
                    this.isLoading.next(false);
                }
            })
        }
        if (this.displaySavedComments) {
            const subscription = this.fetchSavedPosts().pipe().subscribe({
                next: page => {
                    this.loadedPosts = [...this.loadedPosts, ...page.entities];
                    this.totalPages.next(page.totalPages);
                    const pageData = new Page(this.loadedPosts, page.total, page.currentPage, page.totalPages);
                    this.posts.next(pageData);
                    this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
                    this.isLoading.next(false);
                    shareReplay(1);
                }
            });
            this.subscriptions.add(subscription);
        } else {
            const subscription = this.postService.isUserHasSubscriptions(this.username, this.tagName).pipe(
                take(1),
                concatMap((isUserHasSubscriptions: boolean) => {
                    this.isUserSubscribed.next(isUserHasSubscriptions);
                    return this.route.data.pipe(
                        concatMap((data) => {
                            if (data['postData'] && this.currentPostPage.getValue() == 0) {
                                console.log('check');
                                return of(data['postData'] as Page);
                            } else {
                                return this.fetchPosts();
                            }
                        })
                    );
                })).subscribe({
                next: (page) => {
                    this.loadedPosts = [...this.loadedPosts, ...page.entities];
                    this.totalPages.next(page.totalPages);
                    const pageData = new Page(this.loadedPosts, page.total, page.currentPage, page.totalPages);
                    this.posts.next(pageData);
                    this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
                    this.isLoading.next(false);
                    shareReplay(1);
                    this.hasPostsBySubscription();
                },
                error: (error: any) => {
                    this.errorMessage = 'Something went wrong:' + error.error.message
                },
            });
            this.subscriptions.add(subscription);
        }
    }
    hasPostsBySubscription(): void  {
        this.posts.pipe(
            take(1),
            map(postsData => {
                if (postsData.entities.length == 0 && this.isUserSubscribed.getValue()) {
                    this.onPostChange();
                }
            })
        ).subscribe();
    }
    addUpvote(identifier: string) {
        this.postService.addUpvote(identifier).pipe(
            tap(upvotes => {
                this.posts.pipe(
                    take(1),
                    tap(posts => {
                        posts.entities.forEach(post => {
                            if (post.identifier === identifier) {
                                this.isUpvoteMade = true;
                                post.upvotes = upvotes;
                                this.changeDetectorRef.detectChanges()
                            }
                        });
                    })
                ).subscribe();
            })
        ).subscribe();
    }


    removeUpvote(identifier: string) {
        this.postService.removeUpvote(identifier).pipe(
            tap(upvotes => {
                this.posts.pipe(
                    take(1),
                    tap(posts => {
                        posts.entities.forEach(post => {
                            if (post.identifier === identifier) {
                                this.isUpvoteMade = false;
                                post.upvotes = upvotes;
                                this.changeDetectorRef.detectChanges()
                            }
                        });
                    })
                ).subscribe();
            })
        ).subscribe();
    }

    addDownvote(identifier: string) {
        this.postService.addDownvote(identifier).pipe(
            tap(downvotes => {
                this.posts.pipe(
                    take(1),
                    tap(posts => {
                        posts.entities.forEach(post => {
                            if (post.identifier === identifier) {
                                this.isDownVoteMade = true;
                                post.downvotes = downvotes;
                                console.log(post.downvotes)
                                this.changeDetectorRef.detectChanges()
                            }
                        });
                    })
                ).subscribe();
            })
        ).subscribe();
    }

    removeDownvote(identifier: string) {
        this.postService.removeDownvote(identifier).pipe(
            tap(downvotes => {
                this.posts.pipe(
                    take(1),
                    tap(posts => {
                        posts.entities.forEach(post => {
                            if (post.identifier === identifier) {
                                this.isDownVoteMade = false;
                                post.downvotes = downvotes;
                                console.log(post.downvotes)
                                this.changeDetectorRef.detectChanges()
                            }
                        });
                    })
                ).subscribe();
            })
        ).subscribe();
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

    fetchSavedPosts() {
        return this.currentPostPage.pipe(
            take(1),
            distinctUntilChanged(),
            switchMap((currentPage) =>
                this.postService.getSavedPosts(currentPage, this.pageSize, this.sortBy)));
    }

    fetchPosts(): Observable<Page> {
        if (this.searchText) {
            return this.currentPostPage.pipe(
                take(1),
                distinctUntilChanged(),
                switchMap((currentPage) =>
                    this.postService.searchPostsByText(this.searchText, currentPage, this.pageSize, this.sortBy)));
        } else {
            return this.currentPostPage.pipe(
                take(1),
                distinctUntilChanged(),
                switchMap((currentPage) =>
                    this.isUserSubscribed.getValue()
                        ? this.postService.fetchPostsBySubscription(currentPage, this.pageSize)
                        : this.postService.fetchPostsByPage(currentPage, this.tagName, this.username, this.pageSize, this.sortBy)))
        }
    }

    updatePost(postIdentifier: string, title: string, description: string, images: FileDTO) {
        this.matDialogService.updatePost(postIdentifier, title, description, images);
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

    updatePostViewAfterDeletion(identifier: string) {
        if (identifier) {
            const postIndex = this.loadedPosts.findIndex((post) => post.identifier === identifier);
            if (postIndex !== -1) {
                this.loadedPosts.splice(postIndex, 1);
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
        const queryParamsString = "pageSize=" + this.pageSize + "&sortBy=" + this.sortBy;

        const redirectUrl = "post/" + postIdentifier + "?" + queryParamsString;

        this.routingService.setPathVariable(redirectUrl);
        return this.matDialogService.displaySinglePost(postIdentifier);
    }

    ngOnDestroy() {
        this.subscriptions.unsubscribe();
    }
}
