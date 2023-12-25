import {Component, Input} from '@angular/core';
import {AuthService} from "../../../services/auth/auth.service";
import {NotificationService} from "../../../services/entity/notification.service";
import {SubscriptionService} from "../../../services/entity/subscription.service";
import {ValidatorModel} from "../../../model/validator.model";
import {SseRequestService} from "../../../services/request/sse.request.service";
import {map} from "rxjs";
import {UserModel} from "../../../model/user.model";
import {UserRequestService} from "../../../services/request/user.request.service";
import {NotificationRequestService} from "../../../services/request/notification.request.service";

@Component({
    selector: 'app-subscriptions',
    templateUrl: './subscriptions.component.html',
    styleUrls: ['./subscriptions.component.scss']
})
export class SubscriptionsComponent {
    @Input() username: string;
    token: string | null;
    currentUser: string | null;
    isSubscribed: boolean;
    showConfirmation: boolean;
    confirmed: boolean;

    constructor(private notificationService: NotificationService,
                private subscriptionService: SubscriptionService, private requestService: UserRequestService,
                private notificationRequestService: NotificationRequestService) {
        this.username = '';
        this.isSubscribed = false;
        this.currentUser = '';
        this.showConfirmation = false;
        this.confirmed = false;
        this.token = '';
    }

    ngOnInit() {
        this.currentUser = localStorage.getItem('username');
        console.log(this.currentUser)
        if (!this.currentUser) {
            this.requestService.getUser().pipe(map(user => user as UserModel)).subscribe({
                next: user => {
                    localStorage.setItem('username', user.username);
                    this.currentUser = user.username;
                }
            })
        } else if (this.currentUser !== this.username) {
            this.findFollowingByUsername();
        }
    }

    findFollowingByUsername() {
        this.subscriptionService.findFollowingByUsername( this.username).subscribe({
            next: (isSubscribed: ValidatorModel) => {
                this.isSubscribed = isSubscribed.valid;
            }
        })
    }

    addSubscription() {
        this.subscriptionService.addSubscription(this.username).subscribe({
            next: () => {
                this.notificationRequestService.sendNewSubscriptionNotification(this.token, this.username, `${this.currentUser} subscribed on you`)
                    .subscribe({
                        next: () => {
                            this.notificationService.showNotification('Successfully subscribed', false)
                            console.log('check')
                        }
                    })
                this.isSubscribed = true;
            }
        })
    }

    confirmUnsubscribe(): void {
        this.showConfirmation = true;
    }

    onConfirm(): void {
        this.confirmed = true;
        this.showConfirmation = false;
        this.logout();
    }

    onCancel(): void {
        this.showConfirmation = false;
    }

    logout(): void {
        this.subscriptionService.removeSubscription(this.username).subscribe({
            next: () => {
                this.isSubscribed = false;
            }
        });
    }
}
