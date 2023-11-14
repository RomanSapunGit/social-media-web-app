import {ChangeDetectionStrategy, ChangeDetectorRef, Component, HostListener} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {RequestService} from "../../services/request.service";
import {NotificationService} from "../../services/notification.service";
import {BehaviorSubject, fromEvent, map, merge, Observable, of, ReplaySubject, startWith, Subscription} from "rxjs";
import {CommentService} from "../../services/comment.service";
import {UserModel} from "../../model/user.model";
import {TagModel} from "../../model/tag.model";
import {MatDialogService} from "../../services/mat-dialog.service";
import {ActivatedRoute, ActivatedRouteSnapshot} from "@angular/router";
import {RoutingService} from "../../services/routing.service";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";


@Component({
    selector: 'app-mainPage',
    templateUrl: './main-page.component.html',
    styleUrls: ['./main-page.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: []
})

export class MainPageComponent {
    image = 'assets/image/bg1.jpg';
    errorMessage: BehaviorSubject<string>;
    isErrorMessage: boolean;
    scrollTimeout: any;
    networkStatus: Subscription;
    isMobileView: BehaviorSubject<boolean>;
    isMobileNavOpen: boolean;
    selectedList: string;
    private subscriptions: Subscription[];

    constructor(private notificationService: NotificationService,
                private changeDetectorRef: ChangeDetectorRef,
                private matDialogService: MatDialogService, private route: ActivatedRoute,
                private routingService: RoutingService,
               ) {
        this.subscriptions = [];
        this.selectedList = 'posts';
        this.networkStatus = new Subscription();
        this.errorMessage = new BehaviorSubject<string>('');
        this.isErrorMessage = false;
        this.isMobileView = new BehaviorSubject<boolean>(JSON.parse(localStorage.getItem('isMobileView') || 'false'));
        localStorage.setItem('isMobileView', JSON.stringify(this.isMobileView.value));
        this.isMobileNavOpen = false;
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(subscription => subscription.unsubscribe());
        this.networkStatus.unsubscribe();
    }

    ngOnInit() {
        this.subscriptions.push(
        this.matDialogService.isDialogClosed$.subscribe({
            next: (isDialogClosed) => {
                if (isDialogClosed) {
                    this.matDialogService.dialogClosed();
                    this.route.queryParams.subscribe(params => {
                        if(params['pageSize'] && params['sortBy']) {
                            let queryParams = "?pageSize=" + params['pageSize'] + "&sortBy=" + params['sortBy'];
                            this.routingService.clearPathVariable(queryParams);
                        } else {
                            this.routingService.clearPathVariable();
                        }
                    })
                }
            }
        }));
        this.subscriptions.push(
        this.route.paramMap.subscribe(params => {
            let identifier = params.get('id');
            console.log('check')
            if (identifier) {
                this.displaySinglePost(identifier);
            }
        }));
        if (this.isMobileView) {
            const snapshot: ActivatedRouteSnapshot = this.route.snapshot;
            const url: string = snapshot.url.join('/');
            switch (url) {
                case 'main/tags':
                    this.selectedList = 'tags';
                    break;
                case 'main/posts':
                    this.selectedList = 'posts';
                    break;
                case 'main/users':
                    this.selectedList = 'users';
                    break;
                default:
                    this.selectedList = 'posts';
            }
        }
        this.subscriptions.push(
        this.notificationService.notification$.subscribe({
            next: (message) => {
                console.log(message)
                this.errorMessage.next(message.message);
                this.isErrorMessage = message.isErrorMessage;
                this.changeDetectorRef.detectChanges();
            }
        }));
        this.checkNetworkStatus();
    }

    @HostListener('window:resize', ['$event'])
    onResize(event: any) {
        this.isMobileView.next(event.target.innerWidth < 1000);
        localStorage.setItem('isMobileView', JSON.stringify(this.isMobileView.value));
    }


    checkNetworkStatus() {
        let networkStatus = navigator.onLine;
        this.subscriptions.push(
        this.networkStatus = merge(
            of(null),
            fromEvent(window, 'online'),
            fromEvent(window, 'offline')
        )
            .pipe(map(() => navigator.onLine))
            .subscribe(status => {
                console.log('status', status);
                console.log(networkStatus);
            }));
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
    displaySinglePost(postIdentifier: string) {
        this.matDialogService.displaySinglePost(postIdentifier);
    }
}
