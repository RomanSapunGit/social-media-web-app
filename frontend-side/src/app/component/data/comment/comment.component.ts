import {ChangeDetectorRef, Component, Input} from '@angular/core';
import {Observable, take} from "rxjs";
import {Page} from "../../../model/page.model";
import {CommentService} from "../../../service/comment.service";
import {MatDialogService} from "../../../service/mat-dialog.service";

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent {
  @Input() postIdentifier: string;
  @Input() postComments: { [postId: string]: Observable<Page> };
  @Input() currentCommentPage: { [postId: string]: number };
  isCommentPaginationVisible$: Observable<boolean>;
  @Input() commentVisibility:  boolean;

  constructor(private commentService: CommentService, private matDialogService: MatDialogService,
              private changeDetectorRef: ChangeDetectorRef) {
    this.postIdentifier = '';
    this.postComments = {};
    this.currentCommentPage = {};
    this.isCommentPaginationVisible$ = new Observable<boolean>();
    this.commentVisibility = false;
  }

  ngOnInit() {
    this.getComments(this.postIdentifier);
    this.commentService.commentCreated$.subscribe(() => {
      if (this.postIdentifier) {
        this.postComments[this.postIdentifier] = this.postComments[this.postIdentifier].pipe(take(1));
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  getComments(postId: string): void {
    this.postComments[postId] = this.commentService.getComments(postId, this.currentCommentPage[postId]);
    this.isCommentPaginationVisible$ = this.isCommentPaginationVisible(this.postComments[postId]);
  }

  previousCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId];
    if (currentPage > 0) {
      this.currentCommentPage[postId] = currentPage - 1;
      this.getComments(postId);
    }
  }

  nextCommentPage(postId: string) {
    const currentPage = this.currentCommentPage[postId] || 0;
    const postComments = this.postComments[postId];
    if (postComments) {
      postComments.pipe(take(1)).subscribe((page: Page) => {
        const totalPages = page?.pages || 0;
        if (currentPage < totalPages - 1) {
          this.currentCommentPage[postId] = currentPage + 1;
          this.getComments(postId);
        }
      });
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
}
