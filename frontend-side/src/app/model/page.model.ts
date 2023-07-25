import {PostModel} from "./post.model";
import {PageModelInterface} from "./page.model.interface";
import {Inject, Injectable, InjectionToken} from "@angular/core";

export const CONTENT_TOKEN = new InjectionToken<any[]>('content');
export const TOTAL_TOKEN = new InjectionToken<number>('total');
export const CURRENT_PAGE = new InjectionToken<number>('currentPage');

export const PAGES = new InjectionToken<number>('pages');

@Injectable()
export class Page implements PageModelInterface {
  constructor(@Inject(CONTENT_TOKEN) public content: any[],
              @Inject(TOTAL_TOKEN) public total: number,
              @Inject(CURRENT_PAGE) public currentPage: number,
              @Inject(PAGES) public pages: number) {
  }

  create(content: any[], total: number, currentPage: number, pages: number): Page {
    return new Page(content, total, currentPage, pages);
  }
}
