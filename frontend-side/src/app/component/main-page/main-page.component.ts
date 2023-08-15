import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {RequestService} from "../../service/request.service";
import {NotificationService} from "../../service/notification.service";
import {map, Observable} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {CommentService} from "../../service/comment.service";
import {UserModel} from "../../model/user.model";
import {TagModel} from "../../model/tag.model";
import {MatDialogService} from "../../service/mat-dialog.service";
import {ActivatedRoute, NavigationExtras, Router} from "@angular/router";
import {Location} from '@angular/common';
import {RoutingService} from "../../service/routing.service";


@Component({
  selector: 'app-mainPage',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: []
})

export class MainPageComponent {
  image = 'assets/image/bg1.jpg';
  errorMessage: string;
  isErrorMessage: boolean;
  private page: number;
  users: Observable<UserModel[]>;
  tags: Observable<TagModel[]>;
  scrollTimeout: any;

  constructor(private authService: AuthService, private requestService: RequestService,
              private notificationService: NotificationService,
              private commentService: CommentService, private changeDetectorRef: ChangeDetectorRef,
              private matDialogService: MatDialogService, private route: ActivatedRoute,
              private routingService: RoutingService) {
    this.page = 0;
    this.users = this.getUsers();
    this.tags = this.getTags();
    this.errorMessage = '';
    this.isErrorMessage = false;
  }

  ngOnInit() {
    this.matDialogService.isDialogClosed$.subscribe({
      next: (isDialogClosed) => {
        if (isDialogClosed) {
          this.routingService.clearPathVariable();
        }
      }
    })
    this.route.paramMap.subscribe(params => {
      let identifier = params.get('id');
      if (identifier) {
        this.displaySinglePost(identifier);
      }
    });
    this.notificationService.notification$.subscribe({
      next: (message) => {
        this.errorMessage = message.message;
        this.isErrorMessage = message.isErrorMessage;
        this.changeDetectorRef.detectChanges();
      },
      error: (err) => console.log(err.error.message)
    });
  }

  onWindowScroll() {
    const postBox = document.querySelector('.post-list') as HTMLElement;
    if (postBox) {
      postBox.style.userSelect = 'none';
    }

    clearTimeout(this.scrollTimeout);
    this.scrollTimeout = setTimeout(() => {
      if (postBox) {
        postBox.style.userSelect = '';
      }
    }, 500);
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

  displaySinglePost(postIdentifier: string) {
    this.matDialogService.displaySinglePost(postIdentifier);
  }
}
