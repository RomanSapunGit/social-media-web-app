import {Injectable} from '@angular/core';
import {CommentActionComponent} from "../component/dialog/creation-form/comment-action/comment-action.component";
import {ProfileFormComponent} from "../component/dialog/profile-form/profile-form.component";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {PostActionComponent} from "../component/dialog/creation-form/post-action/post-action.component";
import {ErrorDialogComponent} from "../component/dialog/error-dialog/error-dialog.component";
import {PostViewComponent} from "../component/dialog/view-form/post-view.component";
import {BehaviorSubject} from "rxjs";
import {ImageCropperComponent} from "../component/image-cropper/image-cropper.component";

@Injectable({
  providedIn: 'root'
})
export class MatDialogService {
  constructor(private dialog: MatDialog) {
  }

  private isDialogClosed: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  get isDialogClosed$() {
    return this.isDialogClosed;
  }

  createComment(id: string) {
    let dialogConfig = this.setDialogConfigWithData(true, true, '40%', '400px',
      false,
      {id, isUpdating: false});
    this.dialog.open(CommentActionComponent, dialogConfig);
  }

  updateComment(id: string, title: string, description: string) {
    let dialogConfig = this.setDialogConfigWithData(true, true, '40%', '400px',
      false,
      {id, isUpdating: true, title, description});
    this.dialog.open(CommentActionComponent, dialogConfig);
  }

  createPost() {
    let dialogConfig = this.setDialogConfigWithData(true, true, '40%', '80%',
      false, {isUpdating: false});
    this.dialog.open(PostActionComponent, dialogConfig);
  }

  updatePost(postIdentifier: string,title: string, description: string) {
    let dialogConfig = this.setDialogConfigWithData(true, true, '40%', '400px',
      false,
      {isUpdating: true, postIdentifier: postIdentifier, title, description});
    this.dialog.open(PostActionComponent, dialogConfig);
  }

  showPostsByTag(tag: string) {
    let dialogConfig = this.setDialogConfigWithData(false, true, '40%', '650px',
      false, tag);
    this.dialog.open(ProfileFormComponent, dialogConfig);
  }

  showPostsByUsername(username: string) {
    let dialogConfig = this.setDialogConfigWithData(false, true, '40%', '650px',
      false, username);
    this.dialog.open(ProfileFormComponent, dialogConfig);
  }

  displayError(errorMessage: string) {
    let dialogConfig = this.setDialogConfigWithData(false, true, '30%', '150px',
      false, errorMessage);
    this.dialog.open(ErrorDialogComponent, dialogConfig);
  }

  displaySinglePost(identifier: string) {
    let dialogConfig = this.setDialogConfigWithData(false, true, '40%', '650px',
      false, {identifier: identifier});
    const dialogRef = this.dialog.open(PostViewComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
       this.isDialogClosed.next(result.isDialogClosed);
      }
    });
  }

  displayCropper(selectedImage: File) {
    let dialogConfig = this.setDialogConfigWithData(false, true, '40%', '650px',
      false, {selectedImage: selectedImage});
    this.dialog.open(ImageCropperComponent, dialogConfig);
  }

  private setDialogConfigWithData(disableClose: boolean, autofocus: boolean, width: string, height: string, hasBackDrop: boolean, data: any): MatDialogConfig {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = disableClose;
    dialogConfig.autoFocus = autofocus;
    dialogConfig.width = width;
    dialogConfig.height = height;
    dialogConfig.data = data;
    dialogConfig.hasBackdrop = hasBackDrop;
    return dialogConfig;
  }
}
