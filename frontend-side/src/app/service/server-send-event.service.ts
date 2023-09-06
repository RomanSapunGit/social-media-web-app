import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, of} from "rxjs";
import {AuthService} from "./auth.service";
import {RequestService} from "./request.service";
import {UserNotificationModel} from "../model/user-notification.model";
import {PostViewModel} from "../model/post-view.model";

@Injectable({
  providedIn: 'root'
})
export class ServerSendEventService {
  private baseUrl = 'http://localhost:8080';
  constructor(private authService: AuthService, private requestService: RequestService) {
  }

  getNotificationsFromServer(): Observable<MessageEvent> {
    let username = this.authService.getUsername();
    let token = this.authService.getAuthToken();
    if (username && token) {
      const eventSource = new EventSource(`${this.baseUrl}/sse/notifications?username=${username}`);
      return new Observable(observer => {
        eventSource.onmessage = event => {
          console.log("Received message:", event);
          observer.next(event);
        };

        eventSource.onerror = error => {
          observer.error(error);
        };

        return () => {
          eventSource.close();
        };
      });
    }
    return new Observable();
  }

  getNotifications(): Observable<UserNotificationModel[]> {
    let token = this.authService.getAuthToken();
    let username = this.authService.getUsername();
    if (token && username) {
      return this.requestService.getNotifications(token, username).pipe(
          map((response: any) => {
            return response as UserNotificationModel[];
          })
      );
    }
    return new Observable<UserNotificationModel[]>();
  }
  getPostUpdateFromServer(postId: string): Observable<PostViewModel> {
    let username = this.authService.getUsername();
    let token = this.authService.getAuthToken();
    if (username && token) {
      const eventSource = new EventSource(`${this.baseUrl}/sse/posts/updates?postId=${postId}`);
      return new Observable<PostViewModel>(observer => {
        eventSource.onmessage = event => {
          const post = JSON.parse(event.data) as PostViewModel;
          observer.next(post);
        };

        eventSource.onerror = error => {
          observer.error(error);
        };

        return () => {
          eventSource.close();
        };
      });
    }
    return new Observable<PostViewModel>();
  }
}
