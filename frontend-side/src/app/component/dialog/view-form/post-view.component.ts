import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostServiceService} from "../../../service/post-service.service";
import {BehaviorSubject, Observable, ReplaySubject} from "rxjs";
import {PostViewModel} from "../../../model/post-view.model";
import {CacheCommentService} from "../../../service/comment-cache.service";
import {CommentModel} from "../../../model/comment.model";
import {Page} from "../../../model/page.model";

@Component({
  selector: 'app-view-form',
  templateUrl: './post-view.component.html',
  styleUrls: ['./post-view.component.scss']
})
export class PostViewComponent {
  identifier: string;
  postView: ReplaySubject<PostViewModel>;
  commentVisibility: { [postId: string]: boolean };

  constructor(private matDialogRef: MatDialogRef<PostViewComponent>, private postService: PostServiceService,
              @Inject(MAT_DIALOG_DATA) public data: any, private commentCache: CacheCommentService) {
    this.commentVisibility = {};
    this.identifier = data.identifier;
    this.postView = new ReplaySubject<PostViewModel>(1);
  }

  ngOnInit() {
    this.postService.getPostById(this.identifier).subscribe({
        next: (post) => {
          this.postView.next(post);
        }
      }
    )
  }

  toggleCommentsVisibility(postId: string, comments: Page) {
    this.commentCache.save(postId, 0, comments);
    this.commentVisibility[postId] = !this.commentVisibility[postId];
  }

  closeDialog() {
    const resultData = { isDialogClosed: true };
    this.matDialogRef.close(resultData);
  }
}
