import { Injectable } from '@angular/core';
import {BehaviorSubject, ReplaySubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ImageCropperService {
  private croppedImageObjectUrl: ReplaySubject<File> = new ReplaySubject<File>(1);
  private croppedExternalImageUrl: ReplaySubject<File> = new ReplaySubject<File>(1);

  setCroppedImageObjectUrl(objectUrl: File) {
    this.croppedImageObjectUrl.next(objectUrl);
  }

  getCroppedImageObjectUrl$() {
    return this.croppedImageObjectUrl;
  }
  cropImage(imageFile: File) {
      this.croppedExternalImageUrl.next(imageFile);
  }
  getCroppedExternalImageUrl$() {
    return this.croppedExternalImageUrl;
  }
}
