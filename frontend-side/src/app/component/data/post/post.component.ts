import {Component, Inject, Input} from '@angular/core';
import {Page} from "../../../model/page.model";
import {BehaviorSubject, delay, distinctUntilChanged, Observable, take, tap} from "rxjs";
import {PostServiceService} from "../../../service/post-service.service";

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss']
})
export class PostComponent {
  @Input() posts: Observable<Page>;
  currentPostPage: BehaviorSubject<number>;
  isPostPaginationVisible$: Observable<boolean>;
  commentVisibility: { [postId: string]: boolean };

  @Input() tagName: string | null;
  @Input() username: string | null;

  constructor(private postService: PostServiceService) {
    this.posts = new Observable<Page>();
    this.currentPostPage = new BehaviorSubject<number>(0);
    this.isPostPaginationVisible$ = new Observable<boolean>();
    this.commentVisibility = {};
    this.tagName = '';
    this.username = '';
  }

  ngOnInit() {
    this.fetchPostsByPage();
    this.isPostPaginationVisible$ = this.isPostPaginationVisible(this.posts);
  }

  previousPostPage() {
    if (this.currentPostPage.getValue() > 0) {
      this.currentPostPage.next(this.currentPostPage.getValue() - 1);
      this.fetchPostsByPage();
    }
  }

  nextPostPage() {
    this.currentPostPage.pipe(
      take(1)
    ).subscribe((currentPage) => {
      this.currentPostPage.next(currentPage + 1);
      this.fetchPostsByPage();
    });
  }

  fetchPostsByPage() {
    this.currentPostPage.pipe(
      distinctUntilChanged()
    ).subscribe((currentPage) => {
      this.posts = this.postService.fetchPostsByPage(currentPage, this.tagName, this.username);
    });
  }

  isPostPaginationVisible(posts: Observable<Page>): Observable<boolean> {
    return this.postService.isPostPaginationVisible(posts);
  }

  toggleCommentsVisibility(postId: string) {
    this.commentVisibility[postId] = !this.commentVisibility[postId];
  }
}
