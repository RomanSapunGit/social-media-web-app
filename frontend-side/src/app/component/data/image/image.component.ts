import {Component, Input} from '@angular/core';
import {Observable} from "rxjs";
import {FileDTO} from "../../../model/file.model";
import {ImageService} from "../../../service/image.service";

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss']
})
export class ImageComponent {
  @Input() image: FileDTO;
  @Input() images: FileDTO[]
  imagesToDisplay: Observable<string[]>;
  imageToDisplay: Observable<string>;
  showMenu: boolean;
  isClicked: boolean;
  @Input() usernameToDisplay: string;

  public slideConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    infinite: true,
    adaptiveHeight: true,
    lazyLoad: 'ondemand'
  };

  constructor(private imageService: ImageService) {
    this.images = [];
    this.image = new FileDTO();
    this.imagesToDisplay = new Observable<string[]>();
    this.imageToDisplay = new Observable<string>();
    this.showMenu = false;
    this.isClicked = false;
    this.usernameToDisplay = '';
  }

  ngOnInit() {
    if (this.images && this.images.length > 0) {
      this.imagesToDisplay = this.imageService.fetchImagesFromModel(this.images);
    } else if (this.image && this.image.fileType) {
      this.imageToDisplay = this.imageService.fetchImageFromModel(this.image);
    }
  }

  toggleProfileMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  adjustImageSize(event: MouseEvent) {
    const imageElement = event.target as HTMLImageElement;
    const container = document.querySelector('.carousel-container');

    if (!container) return;

    const containerWidth = container.clientWidth;
    const containerHeight = container.clientHeight;

    imageElement.style.maxWidth = `${containerWidth}px`;
    imageElement.style.maxHeight = `${containerHeight}px`;
  }
  closeDropDown() {
    this.showMenu = false;
  }
}
