import {Component, HostListener, Input} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {ImageService} from "../../services/image.service";
import {Observable} from "rxjs";
import {RequestService} from "../../services/request.service";
import {PostService} from "../../services/post.service";
import {MatDialogService} from "../../services/mat-dialog.service";
import {SearchByTextService} from "../../services/search-by-text.service";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";

@Component({
    selector: 'app-navigation-bar',
    templateUrl: './navigation-bar.component.html',
    styleUrls: ['./navigation-bar.component.scss'],
})
export class NavigationBarComponent {
    isNavbarCollapsed = true;
    showProfileMenu: boolean;
    userImage: Observable<string>;
    searchQuery: string;
    isMobileView: boolean | null;
    isMobileNavOpen: boolean;
    isMenuOpen: boolean;
    showConfirmation = false;
    confirmed = false;
    username: string | null;

    constructor(private matDialogService: MatDialogService, private imageService: ImageService, private searchByTextService: SearchByTextService,
                private breakpointObserver: BreakpointObserver, private authService: AuthService) {
        this.showProfileMenu = false;
        this.userImage = new Observable<string>();
        this.searchQuery = '';
        this.isMobileView = this.breakpointObserver.isMatched(Breakpoints.Handset);
        this.isMobileNavOpen = false;
        this.isMenuOpen = false;
        this.username = authService.getUsername();
    }

    @HostListener('window:resize', ['$event'])
    onResize(event: any) {
        this.isMobileView = (event.target.innerWidth < 1000);
    }


    ngOnInit() {
        this.userImage = this.imageService.fetchUserImage();
    }

    openFilterDialog() {
        this.matDialogService.openFilterDialog();
        this.isMenuOpen = false;
    }

    toggleProfileMenu(event: MouseEvent): void {
        event.stopPropagation();
        this.showProfileMenu = !this.showProfileMenu;
    }

    searchPosts() {
        this.searchByTextService.searchByText(this.searchQuery);
    }

    closeDropDown() {
        this.showProfileMenu = false;
    }

    createPost() {
        this.matDialogService.createPost();
    }
    displayPostWindow(username: string | null) {
        if(username) {
            this.matDialogService.showPostsByUsername(username);
            this.isMenuOpen = false;
        }
    }
}
