import {Injectable} from '@angular/core';
import {RequestService} from "./request.service";
import {AuthService} from "./auth.service";
import {map, Observable} from "rxjs";
import {FileDTO} from "../model/file.model";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  imageSrcArray: string[];

  constructor(private requestService: RequestService, private authService: AuthService, private sanitizer: DomSanitizer) {
    this.imageSrcArray = [];
  }

  fetchImagesByPostId(postId: string | null): Observable<SafeResourceUrl[]> {
    let token = this.authService.getAuthToken();
    return this.requestService.getImagesByPostId(token, postId).pipe(
      map((response: any) => {
        return response.map((file: any) => {
          const imageUrl = 'data:'+ file.fileType + ';base64,' + file.fileData;
          return this.sanitizer.bypassSecurityTrustResourceUrl(imageUrl);
        });
      })
    );
  }
}
