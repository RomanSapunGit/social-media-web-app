import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {environment} from "../../../environments/environment";
import {BehaviorSubject, Observable, ReplaySubject, shareReplay, switchMap, take, tap} from "rxjs";
import {ServerSendEventService} from "../../services/server-send-event.service";
import {NotificationModel} from "../../model/notification.model";
import {UserNotificationModel} from "../../model/user-notification.model";
import {AuthService} from "../../services/auth.service";


@Component({
    selector: 'app-user-notification',
    templateUrl: './user-notification.component.html',
    styleUrls: ['./user-notification.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserNotificationComponent {
    notificationCount: BehaviorSubject<number> = new BehaviorSubject<number>(0);
    private subscription: any;
    notifications: ReplaySubject<UserNotificationModel[]>
    @Input() isNotificationsOpened: BehaviorSubject<boolean>;

    constructor(private sseService: ServerSendEventService, private authService: AuthService) {
        this.notifications = new ReplaySubject<UserNotificationModel[]>();
        this.isNotificationsOpened = new BehaviorSubject<boolean>(false);
    }

    ngOnInit(): void {
        this.getNotifications();
        this.subscription = this.sseService.getNotificationsFromServer().subscribe(event => {
            take(1);
            shareReplay(1)
            this.getNotifications();
        });
    }

    getNotifications() {
        this.sseService.getNotifications().pipe(
            take(1),
            tap(newNotifications => {
                this.notifications.next(newNotifications);
                this.notificationCount.next(newNotifications.length);
            })
        ).subscribe();
    }

    toggleNotifications(): void {
        this.isNotificationsOpened.next(!this.isNotificationsOpened.value);
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
        let username = this.authService.getUsername();
        this.sseService.completeSSENotificationConnection( username);
    }
}
