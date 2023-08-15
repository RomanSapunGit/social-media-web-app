import {Component, Inject, Input} from '@angular/core';
import {NotificationService} from "../../../service/notification.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-error-dialog',
  templateUrl: './error-dialog.component.html',
  styleUrls: ['./error-dialog.component.scss']
})
export class ErrorDialogComponent {
   errorMessage: string
  constructor(private snackBarService: NotificationService, private matDialogRef: MatDialogRef<ErrorDialogComponent>,
              @Inject(MAT_DIALOG_DATA) private data: any) {
    this.errorMessage = data.toString();
  }

  closeDialog() {
    this.matDialogRef.close();
  }
}
