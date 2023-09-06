import {Component, Input} from '@angular/core';
import {AuthService} from "../../../service/auth.service";
import {NotificationService} from "../../../service/notification.service";
import {SubscriptionService} from "../../../service/subscription.service";
import {ValidatorModel} from "../../../model/validator.model";

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

    constructor(private authService: AuthService, private notificationService: NotificationService, private subscriptionService: SubscriptionService) {
        this.username = '';
        this.isSubscribed = false;
        this.currentUser = '';
        this.showConfirmation = false;
        this.confirmed = false;
        this.token = '';
    }

    ngOnInit() {
        if (this.authService.getUsername() && this.authService.getAuthToken()) {
            this.currentUser = this.authService.getUsername();
            this.token = this.authService.getAuthToken();
        } else {
            this.notificationService.sendErrorNotificationToSlack("Username not found as a cookie", "in SubscriptionsComponent", new Date());
        }
        this.findFollowingByUsername();
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
                console.log('check');
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
