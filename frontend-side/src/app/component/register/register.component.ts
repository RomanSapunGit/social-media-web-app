import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RequestService} from "../../service/request.service";
import {SnackBarService} from "../../service/snackbar.service";
import {GoogleLoginProvider, SocialAuthService, SocialUser} from "@abacritt/angularx-social-login";
import {Router} from "@angular/router";
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerData = {email: '', password: '', name: '', username: ''};
  errorMessage = '';
  registerForm: FormGroup;
  socialUser!: SocialUser;
  image = 'assets/image/bg1.jpg'

  constructor(private requestService: RequestService, private formBuilder: FormBuilder,
              private snackBarService: SnackBarService, private socialAuthService: SocialAuthService,
              private router: Router, private authService: AuthService) {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.minLength(12)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      name: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit() {
    this.snackBarService.errorMessage$.subscribe(message => {
      if (message.includes('Duplicate entry') && message.includes("'users.username'")) {
        this.errorMessage = 'Username already exists. Please choose a different username.';
      } else if (message.includes('Duplicate entry') && message.includes("'users.email'")) {
        this.errorMessage = "Email already exists. Please choose a different email.";
      } else {
        this.errorMessage = message;
      }
    });
    this.socialAuthService.authState.subscribe((user) => {
      this.socialUser = user;
      if (this.socialUser && this.socialUser.email) {
        const userPayload = {
          email: this.socialUser.email,
          name: this.socialUser.name,
        };
        this.authService.setRegisterUser(userPayload)
        this.router.navigate(['/register/google']).then(() =>
          console.log("redirected to google register page"));
      }
    });
  }

  closeError(): void {
    this.errorMessage = '';
  }

  register() {
    this.registerData = {...this.registerForm.value};
    this.requestService.registerAndRedirect(this.registerData);
  }
}
