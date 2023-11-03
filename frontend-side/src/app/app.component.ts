import {Component} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {AuthService} from "./services/auth.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    isNavbarCollapsed = true;
    selectedLanguage: string = 'en';

    constructor(private translate: TranslateService) {
        const storedLanguage = localStorage.getItem('Language');
        this.selectedLanguage = storedLanguage === null ? 'en' : storedLanguage as string;
        translate.setDefaultLang(this.selectedLanguage);
    }

    switchLanguage() {
        this.translate.use(this.selectedLanguage);
        localStorage.setItem('Language', this.selectedLanguage);
    }

    title = 'frontend-side';

}
