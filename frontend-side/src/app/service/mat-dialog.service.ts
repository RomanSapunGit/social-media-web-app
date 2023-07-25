import { Injectable } from '@angular/core';
import {CommentActionComponent} from "../component/dialog/creation-form/comment-action.component";
import {ViewFormComponent} from "../component/dialog/view-form/view-form.component";
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";

@Injectable({
  providedIn: 'root'
})
export class MatDialogService {
  constructor(private dialog: MatDialog) {
  }

  createComment(id: string) {
    let dialogConfig = this.setDialogConfig(true, true, '60%', '400px',
      { id, isUpdating: false });
    this.dialog.open(CommentActionComponent, dialogConfig);
  }

  updateComment(id: string, title: string, description: string) {
    let dialogConfig = this.setDialogConfig(true, true, '60%', '400px',
      { id, isUpdating: true, title, description });
    this.dialog.open(CommentActionComponent, dialogConfig);
  }

  showPostsByTag(tag: string) {
    let dialogConfig = this.setDialogConfig(false, true, '60%', '650px', tag);
    this.dialog.open(ViewFormComponent, dialogConfig);
  }

  showPostsByUsername(username: string) {
    let dialogConfig = this.setDialogConfig(false, true, '60%', '650px', username);
    this.dialog.open(ViewFormComponent, dialogConfig);
  }

 private setDialogConfig(disableClose: boolean, autofocus: boolean, width: string, height: string, data: any): MatDialogConfig {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = disableClose;
    dialogConfig.autoFocus = autofocus;
    dialogConfig.width = width;
    dialogConfig.height = height;
    dialogConfig.data = data;
    return dialogConfig;
  }
}
