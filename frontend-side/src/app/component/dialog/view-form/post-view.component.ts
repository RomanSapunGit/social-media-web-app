import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostService} from "../../../service/post.service";
import {ReplaySubject, shareReplay, Subscription, take, tap} from "rxjs";
import {PostViewModel} from "../../../model/post-view.model";
import {Page} from "../../../model/page.model";
import {ServerSendEventService} from "../../../service/server-send-event.service";
import {PostModel} from "../../../model/post.model";
import {CommentService} from "../../../service/comment.service";
import {AuthService} from "../../../service/auth.service";

@Component({
    selector: 'app-view-form',
    templateUrl: './post-view.component.html',
    styleUrls: ['./post-view.component.scss'],
})
export class PostViewComponent {
    identifier: string;
    postView: ReplaySubject<PostModel>;
    commentVisibility: { [postId: string]: boolean };
    subscription: Subscription;
    postComments: ReplaySubject<Page>;

    constructor(private matDialogRef: MatDialogRef<PostViewComponent>, private postService: PostService,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private sseService: ServerSendEventService, private commentService: CommentService,
                private authService: AuthService) {
        this.subscription = new Subscription();
        this.commentVisibility = {};
        this.identifier = data.identifier;
        this.postView = new ReplaySubject<PostViewModel>(1);
        this.postComments = new ReplaySubject<Page>(1);
    }

    ngOnInit() {
        this.postService.getPostById(this.identifier).subscribe({
                next: (post) => {
                    this.postView.next(post as PostViewModel);
                }
            }
        )
        this.commentService.getComments(this.identifier, 0).pipe(
            take(1),
            tap(commentPage =>
                this.postComments.next(commentPage)
            )).subscribe();
        this.subscription = this.sseService.getPostUpdateFromServer(this.identifier).subscribe(post => {
            shareReplay(1);
            const postView = post as PostViewModel;
            this.postView.next(postView);
        })
    }

    toggleCommentsVisibility(postId: string) {
        this.commentVisibility[postId] = !this.commentVisibility[postId];
    }

    closeDialog() {
        const resultData = {isDialogClosed: true};
        this.matDialogRef.close(resultData);
        this.subscription.unsubscribe();
        let token = this.authService.getAuthToken();
        this.sseService.completeSSEPostUpdateConnection(token, this.identifier);
    }
}
