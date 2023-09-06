import {ChangeDetectionStrategy, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostServiceService} from "../../../service/post-service.service";
import {map, ReplaySubject, shareReplay, Subscription, take, tap} from "rxjs";
import {PostViewModel} from "../../../model/post-view.model";
import {Page} from "../../../model/page.model";
import {ServerSendEventService} from "../../../service/server-send-event.service";
import {CommentService} from "../../../service/comment.service";

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

    constructor(private matDialogRef: MatDialogRef<PostViewComponent>, private postService: PostServiceService,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private sseService: ServerSendEventService) {
        this.subscription = new Subscription();
        this.commentVisibility = {};
        this.identifier = data.identifier;
        this.postView = new ReplaySubject<PostViewModel>(1);
    }

    ngOnInit() {
        this.postService.getPostById(this.identifier).subscribe({
                next: (post) => {
                    console.log(post.postImages)
                    this.postView.next(post as PostViewModel);
                }
            }
        )
        this.subscription = this.sseService.getPostUpdateFromServer(this.identifier).subscribe(post => {
            shareReplay(1);
            const postView = post as PostViewModel;
            this.postView.next(postView);
        })
    }

    toggleCommentsVisibility(postId: string, comments?: Page) {
        this.commentVisibility[postId] = !this.commentVisibility[postId];
    }
    closeDialog() {
        const resultData = {isDialogClosed: true};
        this.matDialogRef.close(resultData);
        this.subscription.unsubscribe();
    }
}
