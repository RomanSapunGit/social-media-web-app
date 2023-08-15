import {Injectable} from '@angular/core';
import {Page} from "../model/page.model";
import {map, Observable, ReplaySubject} from "rxjs";
import {RequestService} from "./request.service";
import {AuthService} from "./auth.service";
import {PostModel} from "../model/post.model";
import {PostViewModel} from "../model/post-view.model";

@Injectable({
  providedIn: 'root'
})
export class PostServiceService {
  private textFound: ReplaySubject<string>;

  constructor(private authService: AuthService,
              private requestService: RequestService,
              private page: Page) {
    this.textFound = new ReplaySubject<string>()
  }

  get textFound$() {
    return this.textFound;
  }

  searchPostByText(text: string) {
    this.textFound.next(text);
  }

  fetchPostsByPage(page: number, tagName: string | null, username: string | null): Observable<Page> {
    const token = this.authService.getAuthToken();
    let fetchPostsObservable: Observable<any>;

    if (tagName) {
      const codedTagName = tagName.replace(/#/, '%23');
      fetchPostsObservable = this.requestService.getPostsByTag(page, token, codedTagName);
    } else if (username) {
      fetchPostsObservable = this.requestService.getPostsByUsername(page, token, username);
    } else {
      fetchPostsObservable = this.requestService.getPosts(page, token);
    }
    return fetchPostsObservable.pipe(
      map(response => this.convertToPostPage(response))
    );
  }

  fetchPostsBySubscription(page: number): Observable<Page> {
    console.log('check')
    const token = this.authService.getAuthToken();
    return this.requestService.getPostsBySubscription(token, page).pipe(
      map(response => this.convertToPostPage(response))
    );
  }

  isUserHasSubscriptions(): Observable<boolean> {
    let token = this.authService.getAuthToken();
    return this.requestService.isUserHasSubscriptions(token).pipe(map((response: any) => {
      return response.valid;
    }));
  }

  isPostPaginationVisible(posts: Observable<Page>): Observable<boolean> {
    return posts.pipe(
      map(posts => posts?.totalPages !== undefined && posts?.totalPages !== 0 && posts?.totalPages !== 1)
    );
  }

  searchPostsByText(text: string, page: number): Observable<Page> {
    let token = this.authService.getAuthToken();
    return this.requestService.searchPostsByText(token, text, page).pipe(
      map(response => this.convertToPostPage(response))
    );
  }

  getPostById(identifier: string): Observable<PostViewModel> {
    let token = this.authService.getAuthToken();
    return this.requestService.getPostById(token, identifier).pipe(
      map(response => response as PostViewModel)
    )
  }

  private convertToPostPage(response: any): Page {
    return this.page.createPostPage(
      response['entities'] as PostModel[],
      response['total-items'],
      response['current-page'],
      response['totalPages']
    );
  }
}
