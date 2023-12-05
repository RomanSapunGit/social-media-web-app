import {Injectable} from '@angular/core';
import {map, Observable, of, share} from "rxjs";
import {FileDTO} from "../model/file.model";
import {AuthService} from "./auth.service";
import {RequestService} from "./request.service";
import {ImageDtoModel} from "../model/image.dto.model";

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private authService: AuthService, private requestService: RequestService) {
  }

  fetchImagesFromModel(files: ImageDtoModel[]): Observable<string[]> {
    return of(files.map((file: ImageDtoModel) => {
      return 'data:' + file.fileDTO.fileType + ';base64,' +  file.fileDTO.fileData;
    }));
  }

  fetchImageFromModel(file: FileDTO): Observable<string> {
    return of('data:' + file.fileType + ';base64,' + file.fileData);
  }

  fetchUserImage(): Observable<string> {
    let token = this.authService.getAuthToken();
    return this.requestService.getImageByUser(token).pipe(
      map((response: any) => 'data:' + response.fileType + ';base64,' + response.fileData),
      share()
    );
  }
}
