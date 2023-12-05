import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {NotificationService} from "../../services/notification.service";
import {SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {Subscription} from "rxjs";
import {MatDialogService} from "../../services/mat-dialog.service";
import {ImageCropperService} from "../../services/image-cropper.service";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {CredentialsService} from "../../services/credentials.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerData: { email: string, password: string, name: string, username: string };
  registerForm: FormGroup;
  socialUser!: SocialUser;
  authSubscription: Subscription;
  selectedImage: File | null = null;
  image = 'assets/image/bg1.jpg'
  imageUrl = 'assets/image/png-transparent-default-avatar.png';
  message: string;
  isErrorMessage: boolean
  isImageChosen: boolean;

  constructor(private formBuilder: FormBuilder,
              private notificationService: NotificationService, private socialAuthService: SocialAuthService,
              private changeDetectorRef: ChangeDetectorRef, private imageCropperService: ImageCropperService,
              private matDialogService: MatDialogService, private authService: AuthService, private router: Router,
              private credentialsService: CredentialsService) {
    this.isImageChosen = false;
    this.message = '';
    this.isErrorMessage = false;
    this.registerData = {email: '', password: '', name: '', username: ''}
    this.authSubscription = new Subscription();
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.minLength(12)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      name: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

 async ngOnInit() {
    if(this.authService.getAuthToken() && this.authService.getUsername()) {
      await this.router.navigate(['/main'])
    }
    this.notificationService.notification$.subscribe(message => {
      if (message.message.includes('Duplicate entry') && message.message.includes("'users.username'")) {
        this.message = 'Username already exists. Please choose a different username.';
        this.isErrorMessage = message.isErrorMessage;
      } else if (message.message.includes('Duplicate entry') && message.message.includes("'users.email'")) {
        this.message = ('Email already exists. Please choose a different email.');
        this.isErrorMessage = message.isErrorMessage;
      } else {
        this.message = message.message;
        this.isErrorMessage = message.isErrorMessage;
      }
      this.changeDetectorRef.detectChanges();
    });
    this.authSubscription = this.socialAuthService.authState.subscribe((user) => {
      console.log('check')
      this.socialUser = user;
      if (this.socialUser && this.socialUser.email) {
        this.registerForm.controls['email'].setValue(user.email);
        this.registerForm.controls['username'].setValue(user.name);
        this.registerForm.controls['name'].setValue(user.name);
      }
    });
    this.imageCropperService.getCroppedImageObjectUrl$().subscribe({
      next: (imageFile) => {
        this.readImageAsBase64(imageFile);
        this.isImageChosen = false;
      },
      error: (err) => console.log(err.error.message)
    });
  }
  googleSignin(googleUser: any) {
    if (googleUser.credential)
      this.credentialsService.loginViaGoogleAndRedirect(googleUser.credential);
  }
  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
    this.socialAuthService.signOut();
  }

  async register() {
    const { name, username, email, password } = this.registerForm.value;
    const formData = new FormData();
    formData.append('name', name);
    formData.append('username', username);
    formData.append('email', email);
    formData.append('password', password);

    const imageToUpload = this.selectedImage || await this.getCircularImage(this.imageUrl);
    if (imageToUpload) {
      formData.append('image', imageToUpload);
    }

    this.credentialsService.registerAndRedirect(formData)
  }

  async getCircularImage(imageUrl: string): Promise<Blob | null> {
    if (!this.selectedImage) {
      const defaultImageBlob = await this.imageUrlToBlob(imageUrl);
      return defaultImageBlob ? await this.createCircularImage(defaultImageBlob) : null;
    }
    return null;
  }

  onFileSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const file = inputElement.files?.[0];
    if (file) {
      this.matDialogService.displayCropper(file);
    }
  }

  readImageAsBase64(file: File) {
    const reader = new FileReader();
    reader.onload = () => {
      this.imageUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  clearFileInput() {
    const inputElement = document.getElementById('file') as HTMLInputElement;
    inputElement.value = '';
  }

  async createCircularImage(blob: Blob): Promise<Blob | null> {
    return new Promise((resolve, reject) => {
      const image = new Image();
      image.src = URL.createObjectURL(blob);

      image.onload = () => {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');

        if (!context) {
          reject(new Error('Canvas 2D context is not available.'));
          return;
        }

        const size = Math.min(image.width, image.height);
        canvas.width = size;
        canvas.height = size;

        context.beginPath();
        context.arc(size / 2, size / 2, size / 2, 0, 2 * Math.PI);
        context.closePath();
        context.clip();

        context.drawImage(image, (image.width - size) / 2, (image.height - size) / 2, size, size, 0, 0, size, size);

        canvas.toBlob(resolve, 'image/png');
      };

      image.onerror = () => {
        reject(new Error('Failed to load the image.'));
      };
    });
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
}
