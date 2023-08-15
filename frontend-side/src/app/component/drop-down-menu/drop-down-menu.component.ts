import {Component, ElementRef, HostListener, Input, ViewChild} from '@angular/core';
import {MatDialogService} from "../../service/mat-dialog.service";
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-drop-down-menu',
  templateUrl: './drop-down-menu.component.html',
  styleUrls: ['./drop-down-menu.component.scss']
})
export class DropDownMenuComponent {
  @Input() isProfileMenu: boolean;
  showConfirmation: boolean;
  confirmed: boolean;
  username: string | null;
@Input() usernameToDisplay: string;
  constructor(private matDialogService: MatDialogService, private authService: AuthService) {
    this.isProfileMenu = false;
    this.showConfirmation = false;
    this.confirmed = false;
    this.username = authService.getUsername();
    this.usernameToDisplay = '';
  }

  displayPostWindow(username: string | null) {
    if(username) {
      this.matDialogService.showPostsByUsername(username);
    } else {
      console.log('check')
    }
  }

  createPost() {
    this.matDialogService.createPost();
  }

  confirmLogout(): void {
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
    this.authService.logout();
  }
}
