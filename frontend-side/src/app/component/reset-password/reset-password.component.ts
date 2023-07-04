import {Component, OnInit} from '@angular/core';
import {RequestService} from "../../service/request.service";
import {ActivatedRoute} from "@angular/router";
import {SnackBarService} from "../../service/snackbar.service";
import {interval, map, Observable, takeWhile} from "rxjs";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  email: string;
  newPassword = '';
  confirmPassword = '';
  image = 'assets/image/bg1.jpg'
  errorMessage = '';
  timerValue$: Observable<number> = interval(0);
  isTimerRunning$: Observable<boolean> = interval(0).pipe(map(() => false));

  constructor(private route: ActivatedRoute, private requestService: RequestService,
              private snackbar: SnackBarService) {
    this.email = '';
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      console.log(this.token);
    });
    this.snackbar.errorMessage$.subscribe(message => {
      this.errorMessage = message;
    });
  }

  startTimer(seconds: number): void {
    this.timerValue$ = interval(1000).pipe(
      map(count => seconds - count),
      takeWhile(value => value >= 0)
    );

    this.isTimerRunning$ = this.timerValue$.pipe(
      map(value => value > 0)
    );
  }

  submitForm(): void {
    if (this.token) {
      this.submitResetForm();
    } else {
      this.submitForgotForm();
    }
  }

  submitForgotForm(): void {
    this.requestService.forgotPassword(this.email).subscribe(
      {
        next: () => {
          this.snackbar.showNotification('Email sent successfully');
          this.startTimer(60);
        },
        error: (error: any) => {
          this.snackbar.showNotification('Error sending email' + error)
        },
      }
    );
  }

  submitResetForm(): void {
    this.requestService.resetPassword(this.token, this.newPassword, this.confirmPassword).subscribe(
      {
        next: () => {
          this.snackbar.showNotification('Password reset successfully');
        },
        error: (error: any) => {
          this.snackbar.showNotification('Error resetting password' + error)
        },
      }
    );
  }

  confirmPasswordMismatch(): boolean {
    return !(this.confirmPassword === this.newPassword);
  }

  closeError(): void {
    this.errorMessage = '';
  }

}
