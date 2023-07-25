import {Injectable} from '@angular/core';
import {Page} from "../model/page.model";
import {BehaviorSubject, map, Observable, of, switchMap, take} from "rxjs";
import {RequestService} from "./request.service";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class PostServiceService {

  constructor(
    private authService: AuthService,
    private requestService: RequestService,
    private page: Page
  ) {

  }

  fetchPostsByPage(page: number, tagName: string | null, username: string | null): Observable<Page> {
    let token = this.authService.getAuthToken();
    if (tagName) {
      console.log(page);
      let codedTagName = tagName.replace(/#/, '%23');
      return this.requestService.getPostsByTag(page, token, codedTagName).pipe(
        map((response: any) => {
          return this.convertToPage(response);
        })
      );
    } else if (username) {
      return this.requestService.getPostsByUsername(page, token, username).pipe(
        map((response: any) => {
          return this.convertToPage(response);
        })
      );
    } else {
      return this.requestService.getPosts(page, token).pipe(
        map((response: any) => {
          return this.convertToPage(response);
        })
      );
    }
  }

  isPostPaginationVisible(posts: Observable<Page>): Observable<boolean> {
    const posts$ = posts;
    if (posts$) {
      return posts$.pipe(
        map((posts) => posts?.pages !== undefined && posts?.pages !== 0 && posts?.pages !== 1)
      );
    } else {
      return of(false);
    }
  }

  private convertToPage(response: any) {
    return this.page.create(response['entities'], response['total-items'],
      response['current-page'], response['total-pages']);
  }
}
