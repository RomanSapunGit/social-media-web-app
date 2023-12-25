import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {
    map,
    Observable,
    ReplaySubject,
    shareReplay,
    Subscription,
    switchMap,
    take,
    tap
} from "rxjs";
import {Page} from "../../../model/page.model";
import {CommentService} from "../../../services/entity/comment.service";
import {MatDialogService} from "../../../services/mat-dialog.service";
import {AuthService} from "../../../services/auth/auth.service";
import {NotificationService} from "../../../services/entity/notification.service";
import {WebsocketCommentService} from "../../../services/websocket/websocket-comment.service";
import {CommentModel} from "../../../model/comment.model";
import {ImageService} from "../../../services/entity/image.service";
import {CommentRequestService} from "../../../services/request/comment.request.service";
import {NotificationRequestService} from "../../../services/request/notification.request.service";

@Component({
    selector: 'app-comment',
    templateUrl: './comments.component.html',
    styleUrls: ['./comments.component.scss']
})
export class CommentsComponent {
    @Input() postIdentifier: string;
    @Input() postComments: ReplaySubject<Page>;
    currentCommentPage: number;
    isCommentPaginationVisible$: Observable<boolean>;
    @Input() commentVisibility: boolean;
    @Input() displaySavedComments: boolean;
    currentUser: string | null;
    subscription: Subscription;
    savedComments: Map<string, boolean>;
    savedCommentsSubject

    constructor(private commentService: CommentService, private matDialogService: MatDialogService,
                private changeDetectorRef: ChangeDetectorRef, private authService: AuthService, private requestService: CommentRequestService,
                private notificationService: NotificationService, private webSocketService: WebsocketCommentService,
                private imageService: ImageService, private notificationRequestService: NotificationRequestService) {
        this.savedComments = new Map;
        this.postIdentifier = '';
        this.postComments = new ReplaySubject<Page>(1);
        this.currentCommentPage = 0;
        this.isCommentPaginationVisible$ = new Observable<boolean>();
        this.commentVisibility = false;
        this.currentUser = localStorage.getItem('username');
        this.subscription = new Subscription();
        this.displaySavedComments = false;
        this.savedCommentsSubject = new ReplaySubject<Map<string, boolean>>(1);
    }

  async  ngOnInit() {
        if(!this.displaySavedComments) {
            this.webSocketService.connect();
            this.webSocketService.subscribeToCommentActions(this.postIdentifier);
        }
        const sub = await this.webSocketService.getCommentReceived$().pipe(
            switchMap((response: any) => {
                return this.getUserImageAndCreateComment(response).pipe(
                    map(newComment => ({ response, newComment }))
                );
            })
        ).subscribe(({ response, newComment }) => {
            this.postComments.pipe(take(1)).subscribe(page => {
                let comments = page.entities as CommentModel[];
                console.log(page.totalPages);
                console.log(this.currentCommentPage)
                if(page.totalPages ==0 || page.totalPages === this.currentCommentPage +1)
                comments.push(newComment);
                page.entities = comments;
                console.log(page.entities);
            });
        });
        const updateCommentSub = this.webSocketService.getCommentUpdated$().pipe(
            switchMap((response: any) => {
                return this.getUserImageAndCreateComment(response).pipe(
                    map(newComment => ({ response, newComment }))
                );
            })
        ).subscribe(({response, newComment}) => {
            this.postComments.pipe(take(1)).subscribe(page => {
                let comments = page.entities as CommentModel[];
                comments.forEach(comment => {
                    if(comment.identifier === newComment.identifier) {
                        comment.description = newComment.description;
                        comment.title = newComment.title;
                    }
                })
            })
        })
        this.subscription.add(updateCommentSub)
        this.subscription.add(sub)
        this.findCommentsInSavedList();
        if (this.displaySavedComments)
            this.fetchSavedComments();
        this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
           this.commentService.commentCreated$.subscribe((comment) => {
            if (this.postIdentifier) {
                if (comment.postAuthorUsername != comment.username) {
                    let notificationSubscription = this.notificationRequestService.sendCommentNotification(comment.identifier, comment.username + ' commented on your post')
                        .pipe(take(1)).subscribe();
                    this.subscription.add(notificationSubscription);
                }
            } else {
                this.notificationService.sendErrorNotificationToSlack("PostIdentifier not found",
                    "Angular during comment component creation subscription", new Date());
            }
        });
    }

    getUserImageAndCreateComment(response: any): Observable<CommentModel> {
        return this.imageService.getImageByUser(response.username).pipe(
            map(fileDTO => {

                return new CommentModel(
                    response.identifier,
                    response.title,
                    response.description,
                    response.username,
                    response.creationTime,
                    fileDTO
                );
            })
        );
    }

    addCommentToSavedList(identifier: string) {
        this.commentService.addCommentToSavedList(identifier).subscribe({
            next: value => {
                this.savedComments.set(identifier, true);
                this.savedCommentsSubject.next(this.savedComments);
                this.notificationService.showNotification('Comment saved', false)
            },
            error: error => console.log(error.error)
        })
    }

    deleteCommentFromSavedList(identifier: string) {
        this.commentService.deleteCommentFromSavedList(identifier).pipe(take(1)).subscribe({
            next: value => {
                this.savedComments.set(identifier, false);
                this.savedCommentsSubject.next(this.savedComments);
                this.notificationService.showNotification('Comment removed from saved list', false)
            },
            error: error => console.log(error.error)
        })
    }

    findCommentsInSavedList() {
        this.postComments.pipe(
            tap(page => page.entities.forEach(comment => {
                this.findCommentInSavedList(comment.identifier)
            }))
        ).subscribe()
    }

    findCommentInSavedList(identifier: string) {
        return this.commentService.findCommentInSavedList(identifier).pipe(
            shareReplay(1),
            take(1), tap(isSavedComment => {
                this.savedComments.set(identifier, isSavedComment as boolean);
                this.savedCommentsSubject.next(this.savedComments)
            })).subscribe()
    }

    fetchSavedComments() {
        const sub = this.commentService.getSavedComments(this.currentCommentPage).pipe(
            take(1),
            tap(commentPage => this.postComments.next(commentPage))
        ).subscribe()
        this.subscription.add(sub);
        this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
    }

    fetchComments(postId: string): void {
        const sub1 = this.commentService.getComments(postId, this.currentCommentPage).pipe(
            take(1),
            tap(commentPage =>
                this.postComments.next(commentPage)
            )).subscribe();
        this.subscription.add(sub1);
        this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments);
    }

    previousCommentPage(postId: string) {
        const currentPage = this.currentCommentPage;
        if (currentPage > 0) {
            this.currentCommentPage = currentPage - 1;
            this.fetchComments(postId);
        }
    }

    async nextCommentPage(postId: string) {
        const currentPage = this.currentCommentPage || 0;
        const postComments = this.postComments;
        if (postComments) {
            const sub = postComments.pipe(take(1)).subscribe(async (page: Page) => {
                const totalPages = page?.totalPages || 0;
                if (currentPage < totalPages - 1) {
                    this.currentCommentPage = currentPage + 1;
                    await this.fetchComments(postId);
                }
            });
            this.subscription.add(sub);
        }
    }

    createComment(id: string) {
        this.matDialogService.createComment(id);
    }

    updateComment(commentId: string, commentTitle: string, commentDescription: string) {
        this.matDialogService.updateComment(commentId, commentTitle, commentDescription, this.postIdentifier);
    }

    isCommentPaginationVisible(postComments: Observable<Page>): Observable<boolean> {
        return this.commentService.isCommentPaginationVisible(postComments);
    }

    ngOnDestroy() {
        this.savedComments = new Map<string, boolean>();
        this.savedCommentsSubject = new ReplaySubject<Map<string, boolean>>()
        this.subscription.unsubscribe();
        this.postComments = new ReplaySubject<Page>(1);
        console.log('ngOnDestroy works')
        this.webSocketService.disconnect();
        this.commentService.clearSubject();
    }
}
