import {Injectable} from '@angular/core';
import { Observable} from "rxjs";
import {Page} from "../model/page.model";

@Injectable({
  providedIn: 'root'
})
export class CacheCommentService {
  private dbName = 'commentCacheDB';
  private storeName = 'commentCache';

  private db!: IDBDatabase;

  constructor() {
    const request = indexedDB.open(this.dbName, 1);

    request.onerror = (event) => {
      console.error('Error opening IndexedDB');
    };

    request.onsuccess = (event) => {
      this.db = request.result;
    };

    request.onupgradeneeded = (event) => {
      const db = request.result;
      db.createObjectStore(this.storeName);
    };
  }

  load(postId: string, page: number): Observable<Page | null> {
    return new Observable((observer) => {
      const transaction = this.db.transaction(this.storeName, 'readonly');
      const store = transaction.objectStore(this.storeName);
      const request = store.get(`${postId}_${page}`);
      request.onsuccess = (event) => {
        const cachedComment = request.result;
        if (cachedComment) {
          const expiresAt = new Date(cachedComment.expiresAt);
          const currentDate = new Date();
          if (expiresAt > currentDate) {
            observer.next(cachedComment.commentsPage);
          } else {
            this.remove(`${postId}_${page}`);
            observer.next(null);
          }
        } else {
          observer.next(null);
        }
        observer.complete();
      };
      request.onerror = (event) => {
        observer.error(request.error);
      };
    });
  }

  save(postId: string, page: number, commentsPage: Page): void {
    const expirationDate = new Date();
    expirationDate.setDate(expirationDate.getDate() + 1);
    const cacheData = {commentsPage, expiresAt: expirationDate};
    console.log(`${postId}_${page}`)

    const transaction = this.db.transaction(this.storeName, 'readwrite');
    const store = transaction.objectStore(this.storeName);
    const request = store.put(cacheData, `${postId}_${page}`);
  }

  remove(postId: string): void {
    const transaction = this.db.transaction(this.storeName, 'readwrite');
    const store = transaction.objectStore(this.storeName);
    const request = store.delete(postId);
  }
}
