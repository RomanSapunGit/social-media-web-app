import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from "@angular/material/snack-bar";
import {Subject} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class SnackBarService {
  private snackbarSubject: Subject<string> = new Subject<string>();

  constructor(public snackBar: MatSnackBar) {
  }
  get errorMessage$() {
    return this.snackbarSubject.asObservable();
  }

  showNotification(message: string): void {
    this.snackbarSubject.next(message);
  }

}
