import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {RequestService} from "../../service/request.service";
import {PostModel} from "../../model/post.model";
import {SnackBarService} from "../../service/snackbar.service";
import {TimestampDatePipe} from "../../pipe/timestamp-date.pipe";
import { map, Observable} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {CommentService} from "../../service/comment.service";
import {UserModel} from "../../model/user.model";
import {TagModel} from "../../model/tag.model";
import {MatDialogService} from "../../service/mat-dialog.service";

@Component({
  selector: 'app-mainPage',
  templateUrl: './mainPage.component.html',
  styleUrls: ['./mainPage.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [TimestampDatePipe]
})
export class MainPageComponent {
  image = 'assets/image/bg1.jpg';
  errorMessage = '';
  showConfirmation = false;
  confirmed = false;
  private page = 0;
  posts: Observable<PostModel[]>;
  users: Observable<UserModel[]>;
  tags: Observable<TagModel[]>;
  hasPosts: boolean = false;

  constructor(private authService: AuthService, private requestService: RequestService,
              private snackbarService: SnackBarService, private dialog: MatDialog,
              private commentService: CommentService, private changeDetectorRef: ChangeDetectorRef,
              private matDialogService: MatDialogService) {
    this.posts = new Observable<PostModel[]>();
    this.users = new Observable<UserModel[]>();
    this.tags = new Observable<TagModel[]>();
  }

  ngOnInit() {
    this.snackbarService.errorMessage$.subscribe(message => {
      this.errorMessage = message;
    });
    this.users = this.getUsers();
    this.tags = this.getTags();
    this.posts.subscribe((data) => {
      this.hasPosts = data.length == 0;
    });
  }

  getUsers(): Observable<UserModel[]> {
    let token = this.authService.getAuthToken();
    let currentUser = this.authService.getUsername();
    return this.requestService.getUsers(this.page, token).pipe(
      map((response: any) => response['entities'] as UserModel[]),
      map((users: UserModel[]) => users.filter(user => user.username !== currentUser)));
  }

  getTags(): Observable<TagModel[]> {
    let token = this.authService.getAuthToken();
    return this.requestService.getTags(this.page, token).pipe(
      map((response: any) => response['entities'] as TagModel[])
    );
  }

  showPostsByTag(tag: string) {
    this.matDialogService.showPostsByTag(tag);
  }

  showPostsByUsername(username: string) {
    this.matDialogService.showPostsByUsername(username);
  }

  closeError(): void {
    this.errorMessage = '';
  }

  confirmLogout(): void {
    this.showConfirmation = true;
  }

  onConfirm(): void {
    this.confirmed = true;
    this.showConfirmation = false;
    this.logout();
  }

  onCancel(): void {
    this.showConfirmation = false;
  }

  logout(): void {
    this.authService.logout();
  }
}
