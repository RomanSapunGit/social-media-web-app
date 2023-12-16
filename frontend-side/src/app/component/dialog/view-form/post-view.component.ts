import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostService} from "../../../services/post.service";
import {
    BehaviorSubject,
    combineLatest,
    map,
    Observable,
    ReplaySubject,
    shareReplay,
    Subscription,
    switchMap,
    take,
    tap
} from "rxjs";
import {PostViewModel} from "../../../model/post-view.model";
import {Page} from "../../../model/page.model";
import {ServerSendEventService} from "../../../services/server-send-event.service";
import {PostModel} from "../../../model/post.model";
import {CommentService} from "../../../services/comment.service";
import {AuthService} from "../../../services/auth.service";
import {TranslatorService} from "../../../services/translator.service";
import * as confetti from 'canvas-confetti';
import {TranslateService} from "@ngx-translate/core";
import {MatDialogService} from "../../../services/mat-dialog.service";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {NotificationService} from "../../../services/notification.service";

@Component({
    selector: 'app-view-form',
    templateUrl: './post-view.component.html',
    styleUrls: ['./post-view.component.scss'],
})
export class PostViewComponent {
    identifier: string;
    postView: ReplaySubject<PostViewModel>;
    commentVisibility: { [postId: string]: boolean };
    subscription: Subscription;
    postComments: ReplaySubject<Page>;
    isTranslated: boolean;
    previousPostView: ReplaySubject<PostViewModel>;
    isUpvoteMade!: boolean;
    isDownvoteMade!: boolean;
    isMobileView: boolean;
    isSavedPost: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    constructor(private matDialogRef: MatDialogRef<PostViewComponent>, private postService: PostService,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private sseService: ServerSendEventService, private commentService: CommentService,
                private authService: AuthService, private translatorService: TranslatorService,
                private translateService: TranslateService, private matDialogService: MatDialogService,
                private breakpointObserver: BreakpointObserver, private notificationService: NotificationService) {
        this.subscription = new Subscription();
        this.commentVisibility = {};
        this.identifier = data.identifier;
        this.postView = new ReplaySubject<PostViewModel>(1);
        this.postComments = new ReplaySubject<Page>(1);
        this.isTranslated = false;
        this.previousPostView = new ReplaySubject<PostViewModel>(1);
        this.postService.isUpvoteMade(this.identifier).subscribe(isUpvoteMade => {
            this.isUpvoteMade = isUpvoteMade;
        });
        this.isMobileView = this.breakpointObserver.isMatched(Breakpoints.Handset)
        this.postService.isDownvoteMade(this.identifier).subscribe(isDownvoteMade => {
            this.isDownvoteMade = isDownvoteMade;
        });
        this.findPostInSavedList(this.identifier);
    }

    ngOnInit() {
        this.commentService.getComments(this.identifier, 0).pipe(
            take(1),
            tap(commentPage => {
                    console.log(commentPage as Page)
                    this.postComments.next(commentPage)
                }
            )).subscribe();
        this.subscription = this.sseService.getPostUpdateFromServer(this.identifier).subscribe(post => {
            shareReplay(1);
            const postView = post as PostViewModel;
            this.postView.next(postView);
        })
        this.postService.getPostById(this.identifier).subscribe({
            next: (post) => {
                this.previousPostView.next((JSON.parse(JSON.stringify(post))))
                this.postView.next(post as PostViewModel);
                setTimeout(() => {
                    if (this.previousPostView) {
                        this.previousPostView.pipe(tap(post => this.checkTitleForEvents(post))).subscribe()
                    }
                }, 100)
            }
        })
    }

    addPostToSavedList(identifier: string) {
        this.postService.addPostToSavedList(identifier).pipe(take(1)).subscribe({
            next: value => {
                this.notificationService.showNotification('Post saved', false);
                this.isSavedPost.next(true);
            }
        })
    }

    deletePostFromSavedList(identifier: string) {
        this.postService.deletePostFromSavedList(identifier).pipe(take(1)).subscribe({
            next: value => {
                this.notificationService.showNotification('Post deleted from saved list', false);
                this.isSavedPost.next(false);
            }
        })
    }

    findPostInSavedList(identifier: string) {
        return this.postService.findPostInSavedList(identifier).pipe(
            take(1), tap(isSavedPost => this.isSavedPost.next(isSavedPost as boolean))
        ).subscribe()
    }

    addUpvote() {
        this.postService.addUpvote(this.identifier).pipe(
            tap(upvotes => {
                this.postView.pipe(
                    take(1),
                ).subscribe(post => {
                    post.upvotes = upvotes;
                    this.isUpvoteMade = true;
                    console.log(post.upvotes);
                });
            })
        ).subscribe();
    }

    removeUpvote() {
        this.postService.removeUpvote(this.identifier).pipe(
            tap(upvotes => {
                this.postView.pipe(
                    take(1)
                ).subscribe(post => {
                    post.upvotes = upvotes;
                    this.isUpvoteMade = false;
                });
            })
        ).subscribe();
    }

    addDownvote() {
        this.postService.addDownvote(this.identifier).pipe(
            tap(downvotes => {
                this.postView.pipe(
                    take(1)
                ).subscribe(post => {
                    post.downvotes = downvotes
                    this.isDownvoteMade = true;
                })
            })
        ).subscribe();
    }

    removeDownvote() {
        this.postService.removeDownvote(this.identifier).pipe(
            tap(downvotes => {
                this.postView.pipe(
                    take(1)
                ).subscribe(post => {
                    post.downvotes = downvotes
                    this.isDownvoteMade = false;
                });
            })
        ).subscribe();
    }

    checkTitleForEvents(post: PostModel) {
        this.translateService.get('HAPPY_BIRTHDAY').subscribe((translation: string) => {
            if (post.title.startsWith(translation)) {
                this.burstConfetti();
            } else if (post.title.startsWith('Happy birthday')) {

                this.burstConfetti();
            }
        })
    }

    burstConfetti() {
        let canvas = document.getElementById('canvas') as HTMLCanvasElement;
        if (canvas) {
            confetti.create(canvas, {
                resize: true,
                useWorker: true,
            })({particleCount: 200, spread: 200});
        }
    }

    reverseTranslatePost() {
        this.subscription.unsubscribe();
        if (this.previousPostView) {
            this.previousPostView.pipe(tap(previousPost => {
                console.log(previousPost);
                this.postView.next(previousPost);
                this.isTranslated = false;
            })).subscribe()
        }
    }

    translatePost() {
        console.log('check')
        const targetLanguage = localStorage.getItem("Language");
        if (!targetLanguage) return;
        this.subscription = this.postView.pipe(
            take(1),
            switchMap(post => {
                console.log('check')
                const translatedTitle$ = this.translatorService.translateText(post.title);
                const translatedDescription$ = this.translatorService.translateText(post.description);
                return combineLatest([translatedTitle$, translatedDescription$]).pipe(
                    tap(([title, description]) => {
                        if (title.translatedText && description.translatedText) {
                            const translatedPost = {
                                ...post,
                                title: title.translatedText,
                                description: description.translatedText
                            };
                            this.postView.next(translatedPost);
                        }
                    })
                );
            })
        ).subscribe();
        this.isTranslated = true;
    }

    toggleCommentsVisibility(postId: string) {
        this.commentVisibility[postId] = !this.commentVisibility[postId];
    }

    closeDialog() {
        this.matDialogService.dialogClosed();
        const resultData = {isDialogClosed: true};
        this.matDialogRef.close(resultData);
        this.subscription.unsubscribe();
        this.sseService.completeSSEPostUpdateConnection(this.identifier);
    }
}
