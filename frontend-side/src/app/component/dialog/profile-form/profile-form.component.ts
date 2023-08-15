import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'profile-view-form',
  templateUrl: './profile-form.component.html',
  styleUrls: ['./profile-form.component.scss']
})
export class ProfileFormComponent {
  currentPostPage: number;
  currentCommentPage: { [postId: string]: number };
  tagName: string | null;
  username: string | null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private matDialogRef: MatDialogRef<ProfileFormComponent>
  ) {
    this.currentPostPage = 0;
    this.currentCommentPage = {};
    this.tagName = '';
    this.username = '';
    this.currentCommentPage = {};
  }

  ngOnInit() {
    this.tagName = this.data.startsWith('#') ? this.data : '';
    this.username = !this.tagName ? this.data : '';
  }

  closeDialog(): void {
    this.matDialogRef.close();
  }
}
