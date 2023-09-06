import {Component, Inject} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../../../service/request.service";
import {AuthService} from "../../../../service/auth.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostActionService} from "../../../../service/post-action.service";
import {PostModel} from "../../../../model/post.model";
import {MatDialogService} from "../../../../service/mat-dialog.service";
import {NotificationService} from "../../../../service/notification.service";

@Component({
  selector: 'app-post-action',
  templateUrl: './post-action.component.html',
  styleUrls: ['./post-action.component.scss']
})
export class PostActionComponent {
  postData: { title: string; description: string; images: File[]; }
  postForm: FormGroup;
  selectedImages: File[];
  imageURLS: any[];
  postIdentifier: string;
  isUpdating: boolean;
  isImageClicked: boolean;
  hoverIndex: number | null = null;
  imageUrl = 'assets/image/default-post-image.png';

  constructor(private formBuilder: FormBuilder, private requestService: RequestService, private authService: AuthService,
              @Inject(MAT_DIALOG_DATA) public data: any, public dialogRef: MatDialogRef<PostActionComponent>,
              private postActionService: PostActionService, private matDialogService: MatDialogService,
              private notificationService: NotificationService) {
    this.selectedImages = [];
    this.imageURLS = [];
    this.isImageClicked = false;
    this.postData = {title: '', description: '', images: []};
    this.postIdentifier = this.data.postIdentifier;
    this.isUpdating = this.data.isUpdating;
    this.postForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(6)]],
      description: ['', [Validators.required, Validators.minLength(6)]],
      images: [],
    });
    if (this.isUpdating) {
      this.postForm.patchValue({
        title: data.title,
        description: data.description
      });
    }
  }

  onSubmit() {
    this.postData = {...this.postForm.value};
    let token = this.authService.getAuthToken();
    this.isUpdating ? this.updatePost(token) : this.createPost(token);
    this.selectedImages = [];
    this.imageURLS = [];
    this.closeDialog();
  }

  async updatePost(token: string | null) {
    const formData = new FormData();
    formData.append('identifier', this.postIdentifier);
    formData.append('title', this.postData.title);
    formData.append('description', this.postData.description);
    formData.append('requestPostDTO', new Blob([JSON.stringify(this.postData)], {type: "application/json"}));
    if (this.postData.images) {
      this.postData.images.forEach((image: File) => {
        formData.append('images', image);
      });
    } else {
      const imageToUpload = await this.imageUrlToBlob(this.imageUrl);
      formData.append('images', imageToUpload ?? '');
    }
    this.requestService.updatePost(token, formData).subscribe({
      next: response => {
        this.postActionService.addPost(response as PostModel)
        this.notificationService.showNotification('Post updated', false);
      }
    });
  }

  async createPost(token: string | null) {
    const formData = new FormData();
    formData.append('identifier', '');
    formData.append('title', this.postData.title);
    formData.append('description', this.postData.description);
    if (this.postData.images) {
      this.postData.images.forEach((image: File) => {
        formData.append('images', image, image.name);
      });
    } else {
      const imageToUpload = await this.imageUrlToBlob(this.imageUrl);
      formData.append('images', imageToUpload ?? '');
    }
    this.requestService.createPost(token, formData).subscribe({
      next: response => {
        this.postActionService.addPost(response as PostModel);
        this.notificationService.showNotification('Post was successfully created', false);
      }
    });
  }

  onImageSelected(event: Event) {
    const element = event.target as HTMLInputElement;
    const files = element.files;
    if (files) {
      for (let i = 0; i < files.length; i++) {
        const file = files.item(i);
        if (file) {
          this.selectedImages.push(file);
          const reader = new FileReader();
          reader.onload = (e: any) => {
            if (e && e.target && e.target.result) {
              this.imageURLS.push(e.target.result);
              this.postForm.controls['images'].setValue(this.selectedImages);
            }
          };
          reader.readAsDataURL(file);
        }
      }
    }
  }

  deleteImage(index: number) {
    this.selectedImages.splice(index, 1);
    this.imageURLS.splice(index, 1);
    this.postForm.controls['images'].setValue(this.selectedImages);
    this.isImageClicked = false;
  }

  async imageUrlToBlob(url: string): Promise<Blob | null> {
    try {
      const response = await fetch(url);
      if (!response.ok) {
        return null;
      }
      return await response.blob();
    } catch (error) {
      console.error('Error fetching image:', error);
      return null;
    }
  }
  closeDialog() {
    this.dialogRef.close();
  }
}
