import {Injectable} from '@angular/core';
import {Subject} from "rxjs";
import {NotificationModelInterface} from "../model/notification.model.interface";


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSubject: Subject<NotificationModelInterface> = new Subject<NotificationModelInterface>();

  constructor() {
  }

  get notification$() {
    return this.notificationSubject.asObservable();
  }

  showNotification(message: string, isError: boolean): void {
    this.notificationSubject.next(new NotificationModelInterface(message, isError));
  }
}
