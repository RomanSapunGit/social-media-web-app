import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SocialAuthService} from "@abacritt/angularx-social-login";
import {AuthService} from "../../service/auth.service";
import {RequestService} from "../../service/request.service";

@Component({
  selector: 'app-register-via-google',
  templateUrl: './register-via-google.component.html',
  styleUrls: ['./register-via-google.component.scss']
})
export class RegisterViaGoogleComponent {
  registerData = {email: '', username: '', password: '', name: ''};
  registerForm!: FormGroup;
  user: any;
  image = 'assets/image/bg1.jpg'
  errorMessage = '';

  constructor(private formBuilder: FormBuilder, private socialAuthService: SocialAuthService,
              private authService: AuthService, private requestService: RequestService) {
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      email: ['', Validators.required],
      name: ['', [Validators.required, Validators.minLength(6)]],
      username: ['', [Validators.required, Validators.minLength(6)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
    this.user = JSON.parse(this.authService.getRegisterUser());
    this.registerForm.controls['email'].setValue(this.user.email);
    this.registerForm.controls['username'].setValue(this.user.name);
    this.registerForm.controls['name'].setValue(this.user.name);
  }

  register() {
    this.registerData = {...this.registerForm.value};
    this.authService.clearRegisterUser()
    this.requestService.registerAndRedirect(this.registerData);
  }

  closeError(): void {
    this.errorMessage = '';
  }
}
