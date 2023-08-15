import {PageModelInterface} from "./page.model.interface";
import {Inject, Injectable, InjectionToken} from "@angular/core";
import {PostModel} from "./post.model";
import {PostViewModel} from "./post-view.model";

export const CONTENT_TOKEN = new InjectionToken<any[]>('entities');
export const TOTAL_TOKEN = new InjectionToken<number>('total');
export const CURRENT_PAGE = new InjectionToken<number>('currentPage');

export const PAGES = new InjectionToken<number>('totalPages');

@Injectable()
export class Page implements PageModelInterface {
  constructor(@Inject(CONTENT_TOKEN) public entities: any[],
              @Inject(TOTAL_TOKEN) public total: number,
              @Inject(CURRENT_PAGE) public currentPage: number,
              @Inject(PAGES) public totalPages: number) {
  }

  create(entities: any[], total: number, currentPage: number, totalPages: number): Page {
    return new Page(entities, total, currentPage, totalPages);
  }

  createPostPage(entities: PostModel[], total: number, currentPage: number, totalPages: number): Page {
    return new Page(entities, total, currentPage, totalPages);
  }
}
