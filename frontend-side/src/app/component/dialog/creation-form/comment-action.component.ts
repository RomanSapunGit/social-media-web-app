import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../../service/request.service";
import {AuthService} from "../../../service/auth.service";
import {SnackBarService} from "../../../service/snackbar.service";
import {CommentModel} from "../../../model/comment.model";
import {CommentService} from "../../../service/comment.service";
import {data} from "autoprefixer";

@Component({
  selector: 'app-creation-form',
  templateUrl: './comment-action.component.html',
  styleUrls: ['./comment-action.component.scss']
})
export class CommentActionComponent {
  commentForm: FormGroup;
  commentData = {title: '', description: ''};
  errorMessage = '';
  isUpdating: boolean;


  constructor(public dialogRef: MatDialogRef<CommentActionComponent>, private formBuilder: FormBuilder,
              private requestService: RequestService, private authService: AuthService,
              private snackBarService: SnackBarService, private commentService: CommentService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.commentForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(6)]],
      description: ['', [Validators.required, Validators.minLength(6)]]
    })
    this.isUpdating = this.data.isUpdating;
    if (this.isUpdating) {
      this.commentForm.patchValue({
        title: data.title,
        description: data.description
      });
    }
  }

  ngOnInit() {
    this.snackBarService.errorMessage$.subscribe(message => {
      this.errorMessage = message;
    })
  }
  onSubmit() {
    this.isUpdating ? this.updateComment() : this.createComment();
  }

  onClose() {
    this.dialogRef.close();
  }
  createComment() {
    this.commentData = {...this.commentForm.value};
    let postId = this.data.id;
    let token = this.authService.getAuthToken();
    this.requestService.createComment(postId, token, this.commentData).subscribe({
        error: (error: any) => console.log('Something went wrong:' + error),
        next: (response: any) => {
          this.commentService.addComment(response as CommentModel);
          this.onClose();
        }
      }
    );
  }

  updateComment() {
    this.commentData = {...this.commentForm.value};
    let commentId = this.data.id;
    let token = this.authService.getAuthToken();
    this.requestService.updateComment(this.commentData, token, commentId).subscribe({
      error: (error: any) => console.log('Something went wrong:' + error),
      next: (response: any) => {
        this.commentService.addComment(response as CommentModel);
        this.onClose();
      }
    })
  }

  closeError(): void {
    this.errorMessage = '';
  }
}
