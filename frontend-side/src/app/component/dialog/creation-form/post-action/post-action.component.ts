import {Component, Inject, Input} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../../../services/request.service";
import {AuthService} from "../../../../services/auth.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PostActionService} from "../../../../services/post-action.service";
import {PostModel} from "../../../../model/post.model";
import {MatDialogService} from "../../../../services/mat-dialog.service";
import {NotificationService} from "../../../../services/notification.service";
import {PostService} from "../../../../services/post.service";
import {FileDTO} from "../../../../model/file.model";
import {DomSanitizer} from "@angular/platform-browser";
import {CustomFile} from "../../../../model/custom.file.model";
import {ImageDtoModel} from "../../../../model/image.dto.model";
import {RequestUpdatePostModel} from "../../../../model/request-update-post.model";
import {RequestImageDtoModel} from "../../../../model/request-image-dto.model";
import {RequestFileDtoModel} from "../../../../model/response-file-dto.model";

@Component({
    selector: 'app-post-action',
    templateUrl: './post-action.component.html',
    styleUrls: ['./post-action.component.scss']
})
export class PostActionComponent {
    @Input() existedImages: ImageDtoModel[];
    postData: { title: string; description: string; images: CustomFile[]; }
    postForm: FormGroup;
    selectedImages: CustomFile[];
    imageURLS: any[];
    postIdentifier: string;
    isUpdating: boolean;
    isImageClicked: boolean;
    hoverIndex: number | null = null;
    imageUrl = 'assets/image/default-post-image.png';
    isPostDeletionSubmitted: boolean;

    constructor(private formBuilder: FormBuilder, private requestService: RequestService, private authService: AuthService,
                @Inject(MAT_DIALOG_DATA) public data: any, public dialogRef: MatDialogRef<PostActionComponent>,
                private postActionService: PostActionService, private matDialogService: MatDialogService,
                private notificationService: NotificationService, private postService: PostService,
                private _sanitizer: DomSanitizer) {
        this.existedImages = data.images;
        this.isPostDeletionSubmitted = false;
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

    ngOnInit() {
        this.selectedImages = this.convertFileDTOsToFiles(this.existedImages, "existed");
        this.imageURLS = this.existedImages.map(fileDTO => {
            const base64String = fileDTO.fileDTO.fileData;
            return this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpeg;base64,' + base64String);
        });
        this.selectedImages.forEach(image => console.log(image))
    }

    private convertFileDTOsToFiles(fileDTOs: ImageDtoModel[], source: 'existed' | 'new'): CustomFile[] {
        return fileDTOs.map(fileDTO => {
            const blob = new Blob([fileDTO.fileDTO.fileData], {type: fileDTO.fileDTO.fileType});
            const file = new File([blob], fileDTO.fileDTO.fileName, {type: fileDTO.fileDTO.fileType});
            if (fileDTO.identifier) {
                return new CustomFile(file, source, fileDTO.identifier);
            }
            return new CustomFile(file, source);
        });
    }

    onSubmit() {
        this.postData = {...this.postForm.value};
        let token = this.authService.getAuthToken();
        this.isUpdating ? this.updatePost() : this.createPost(token);
        this.selectedImages = [];
        this.imageURLS = [];
        this.closeDialog();
    }

    deletePost() {
        this.postService.deletePost(this.postIdentifier).subscribe();
        this.postActionService.deletePost(this.postIdentifier);
        this.closeDialog();
    }

    onConfirmPostDeletion() {
        this.isPostDeletionSubmitted = true;
    }

    onConfirm(): void {
        this.deletePost();
    }

    onCancel(): void {
        this.isPostDeletionSubmitted = false;
    }

    async updatePost() {
        const requestUpdatePostDTO: RequestUpdatePostModel = {
            identifier: this.postIdentifier,
            title: this.postData.title,
            description: this.postData.description,
            images: await Promise.all(
                this.postData.images
                    .filter((image: CustomFile) => image.source === 'existed')
                    .map(async (image: CustomFile) => {
                        const arrayBuffer = await this.readFileAsArrayBuffer(image.file);
                        const base64String = this.arrayBufferToBase64(arrayBuffer);
                        return new RequestImageDtoModel(
                            image.identifier || '',
                            new RequestFileDtoModel(
                                image.file.name,
                                image.file.type,
                                base64String
                            )
                        );
                    })
            ),
            newImages: await Promise.all(
                this.postData.images
                    .filter((image: CustomFile) => image.source === 'new')
                    .map(async (image: CustomFile) => {
                        const arrayBuffer = await this.readFileAsArrayBuffer(image.file);
                        const base64String = this.arrayBufferToBase64(arrayBuffer);
                        return new RequestImageDtoModel(
                            image.identifier || '',
                            new RequestFileDtoModel(
                                image.file.name,
                                image.file.type,
                                base64String
                            )
                        );
                    })
            )
        };

        const response = await this.requestService.updatePost(requestUpdatePostDTO).toPromise();
        this.postActionService.addPost(response as PostModel)
        this.notificationService.showNotification('Post updated', false);
    }

   private arrayBufferToBase64(buffer: any) {
       let binary = '';
       let bytes = new Uint8Array(buffer);
       let len = bytes.byteLength;
        for (let i = 0; i < len; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return window.btoa(binary);
    }


    private async readFileAsArrayBuffer(file: File): Promise<ArrayBuffer> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = (event) => {
                if (event.target && event.target.result instanceof ArrayBuffer) {
                    resolve(event.target.result);
                } else {
                    reject(new Error('Failed to read file as ArrayBuffer'));
                }
            };
            reader.readAsArrayBuffer(file);
        });
    }

    async createPost(token: string | null) {
        const formData = new FormData();
        formData.append('identifier', '');
        formData.append('title', this.postData.title);
        formData.append('description', this.postData.description);
        if (this.postData.images) {
            this.postData.images.forEach((image: CustomFile) => {
                formData.append('images', image.file, image.file.name);
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
                    const customFile = new CustomFile(file, "new")
                    this.selectedImages.push(customFile);
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
        this.matDialogService.dialogClosed();
        this.dialogRef.close();
    }
}
