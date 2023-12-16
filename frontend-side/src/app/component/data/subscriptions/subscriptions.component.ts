import {Component, Input} from '@angular/core';
import {AuthService} from "../../../services/auth.service";
import {NotificationService} from "../../../services/notification.service";
import {SubscriptionService} from "../../../services/subscription.service";
import {ValidatorModel} from "../../../model/validator.model";
import {RequestService} from "../../../services/request.service";
import {map} from "rxjs";
import {UserModel} from "../../../model/user.model";

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
                private subscriptionService: SubscriptionService, private requestService: RequestService) {
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
        this.subscriptionService.findFollowingByUsername(this.token, this.username).subscribe({
            next: (isSubscribed: ValidatorModel) => {
                this.isSubscribed = isSubscribed.valid;
            }
        })
    }

    addSubscription() {
        this.subscriptionService.addSubscription(this.username, this.token).subscribe({
            next: () => {
                this.requestService.sendNewSubscriptionNotification(this.token, this.username, `${this.currentUser} subscribed on you`)
                    .subscribe({
                        next: () => {
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
        this.subscriptionService.removeSubscription(this.username, this.token).subscribe({
            next: () => {
                this.isSubscribed = false;
            }
        });
    }
}
