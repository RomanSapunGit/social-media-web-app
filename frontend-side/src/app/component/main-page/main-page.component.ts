import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {RequestService} from "../../service/request.service";
import {NotificationService} from "../../service/notification.service";
import {fromEvent, map, merge, Observable, of, Subscription} from "rxjs";
import {CommentService} from "../../service/comment.service";
import {UserModel} from "../../model/user.model";
import {TagModel} from "../../model/tag.model";
import {MatDialogService} from "../../service/mat-dialog.service";
import {ActivatedRoute} from "@angular/router";
import {RoutingService} from "../../service/routing.service";
import {SearchByTextService} from "../../service/search-by-text.service";
import {ServerSendEventService} from "../../service/server-send-event.service";


@Component({
    selector: 'app-mainPage',
    templateUrl: './main-page.component.html',
    styleUrls: ['./main-page.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: []
})

export class MainPageComponent {
    image = 'assets/image/bg1.jpg';
    errorMessage: string;
    isErrorMessage: boolean;
    private page: number;
    users: Observable<UserModel[]>;
    tags: Observable<TagModel[]>;
    scrollTimeout: any;
    networkStatus$: Subscription = Subscription.EMPTY;

    constructor(private authService: AuthService, private requestService: RequestService,
                private notificationService: NotificationService,
                private commentService: CommentService, private changeDetectorRef: ChangeDetectorRef,
                private matDialogService: MatDialogService, private route: ActivatedRoute,
                private routingService: RoutingService, private searchByTextService: SearchByTextService,) {
        this.page = 0;
        this.users = this.getUsers();
        this.tags = this.getTags();
        this.errorMessage = '';
        this.isErrorMessage = false;
    }

    ngOnDestroy(): void {
        this.networkStatus$.unsubscribe();

    }

    ngOnInit() {
        this.searchByTextService.textFound$.subscribe({
            next: (text) => {
                if (!text) {
                    this.tags = this.getTags();
                    this.users = this.getUsers();
                } else {
                    this.tags = this.getTagsByText(text);
                    this.users = this.getUsersByText(text);
                }
            }
        })
        this.matDialogService.isDialogClosed$.subscribe({
            next: (isDialogClosed) => {
                if (isDialogClosed) {
                    this.routingService.clearPathVariable();
                }
            }
        })
        this.route.paramMap.subscribe(params => {
            let identifier = params.get('id');
            if (identifier) {
                this.displaySinglePost(identifier);
            }
        });
        this.notificationService.notification$.subscribe({
            next: (message) => {
                this.errorMessage = message.message;
                this.isErrorMessage = message.isErrorMessage;
                this.changeDetectorRef.detectChanges();
            }
        });
        this.checkNetworkStatus();
    }

    checkNetworkStatus() {
        let networkStatus = navigator.onLine;
        this.networkStatus$ = merge(
            of(null),
            fromEvent(window, 'online'),
            fromEvent(window, 'offline')
        )
            .pipe(map(() => navigator.onLine))
            .subscribe(status => {
                console.log('status', status);
                console.log(networkStatus);
            });
    }

    onWindowScroll() {
        const postBox = document.querySelector('.post-list') as HTMLElement;
        if (postBox) {
            postBox.style.userSelect = 'none';
        }

        clearTimeout(this.scrollTimeout);
        this.scrollTimeout = setTimeout(() => {
            if (postBox) {
                postBox.style.userSelect = '';
            }
        }, 500);
    }

    getUsers(): Observable<UserModel[]> {
        let token = this.authService.getAuthToken();
        let currentUser = this.authService.getUsername();
        return this.requestService.getUsers(this.page, token).pipe(
            map((response: any) => response as UserModel[]),
            map((users: UserModel[]) => users.filter(user => user.username !== currentUser)));
    }

    getUsersByText(text: string): Observable<UserModel[]> {
        let token = this.authService.getAuthToken();
        let currentUser = this.authService.getUsername();
        return this.requestService.getUsersByText(token, text, this.page).pipe(
            map((response: any) => response['entities'] as UserModel[]),
            map((users: UserModel[]) => users.filter(user => user.username !== currentUser))
        );
    }

    getTagsByText(text: string): Observable<TagModel[]> {
        let token = this.authService.getAuthToken();
        return this.requestService.getTagsByText(token, text).pipe(
            map((response: any) => response as TagModel[])
        );
    }

    getTags(): Observable<TagModel[]> {
        let token = this.authService.getAuthToken();
        return this.requestService.getTags(this.page, token).pipe(
            map((response: any) => response as TagModel[])
        );
    }

    showPostsByTag(tag: string) {
        this.matDialogService.showPostsByTag(tag);
    }

    showPostsByUsername(username: string) {
        this.matDialogService.showPostsByUsername(username);
    }

    displaySinglePost(postIdentifier: string) {
        this.matDialogService.displaySinglePost(postIdentifier);
    }
}
