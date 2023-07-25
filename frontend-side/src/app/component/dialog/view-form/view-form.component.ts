import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-view-form',
  templateUrl: './view-form.component.html',
  styleUrls: ['./view-form.component.scss']
})
export class ViewFormComponent {
  currentPostPage: number;
  currentCommentPage: { [postId: string]: number };
  tagName: string | null;
  username: string | null;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any
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
}
