import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-mainPage',
  templateUrl: './mainPage.component.html',
  styleUrls: ['./mainPage.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainPageComponent {
  image = 'assets/image/bg1.jpg'

  showConfirmation = false;
  confirmed = false;

  constructor(private authService: AuthService) {}

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
