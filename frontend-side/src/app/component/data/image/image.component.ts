import {Component, Input} from '@angular/core';
import {Observable} from "rxjs";
import {ImageService} from "../../../service/image.service";
import {SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss']
})
export class ImageComponent {
 @Input() postIdentifier: string
  images: Observable<SafeResourceUrl[]>;
  constructor(private imageService: ImageService) {
   this.postIdentifier = '';
    this.images = new Observable<SafeResourceUrl[]>();
  }

  ngOnInit() {
    this.images = this.imageService.fetchImagesByPostId(this.postIdentifier);
  }
}
