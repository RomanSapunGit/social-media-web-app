import {Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {ImageService} from "../../service/image.service";
import {Observable} from "rxjs";
import {RequestService} from "../../service/request.service";
import {PostServiceService} from "../../service/post-service.service";
import {MatDialogService} from "../../service/mat-dialog.service";

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

  constructor(private authService: AuthService, private imageService: ImageService, private postService: PostServiceService) {
    this.showProfileMenu = false;
    this.userImage = new Observable<string>();
    this.searchQuery = '';
  }

  ngOnInit() {
    this.userImage = this.imageService.fetchUserImage();
  }

  toggleProfileMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showProfileMenu = !this.showProfileMenu;
  }

  searchPosts() {
      this.postService.searchPostByText(this.searchQuery);
  }
  closeDropDown() {
    this.showProfileMenu = false;
  }
}
