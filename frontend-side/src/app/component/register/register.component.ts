import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../services/request.service";
import {NotificationService} from "../../services/notification.service";
import {SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {Subscription} from "rxjs";
import {MatDialogService} from "../../services/mat-dialog.service";
import {ImageCropperService} from "../../services/image-cropper.service";
import {ServerSendEventService} from "../../services/server-send-event.service";
import {environment} from "../../../environments/environment";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";

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

  constructor(private requestService: RequestService, private formBuilder: FormBuilder,
              private notificationService: NotificationService, private socialAuthService: SocialAuthService,
              private changeDetectorRef: ChangeDetectorRef, private imageCropperService: ImageCropperService,
              private matDialogService: MatDialogService, private authService: AuthService, private router: Router) {
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


  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  async register() {
    this.registerData = {...this.registerForm.value};
    let formData = new FormData();
    formData.append('name', this.registerData.name);
    formData.append('username', this.registerData.username);
    formData.append('email', this.registerData.email);
    formData.append('password', this.registerData.password);

    const imageToUpload = this.selectedImage || await this.imageUrlToBlob(this.imageUrl);
    if (imageToUpload) {
      formData.append('image', imageToUpload);
    }

    this.requestService.register(formData).subscribe({
      next: () => {
        this.requestService.loginAndRedirect(this.registerData);
        this.notificationService.showNotification('User registered successfully', false);
      }
    });
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
