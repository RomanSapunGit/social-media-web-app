import {Injectable} from "@angular/core";
import {RequestService} from "./request.service";
import {NotificationService} from "./notification.service";
import {Observable, of, tap} from "rxjs";
import {AuthService} from "./auth.service";
import {TranslationModel} from "../model/translation.model";

@Injectable({
    providedIn: 'root'
})
export class TranslatorService {
    constructor(private requestService: RequestService, private authService: AuthService) {
    }

    translateText(text: string): Observable<TranslationModel> {
        let token = this.authService.getAuthToken();
        let targetLanguage = localStorage.getItem("Language") == 'ua' ? 'uk' : localStorage.getItem("Language");
        if (token) {
            let observer = targetLanguage ? this.requestService.translateText(text, token, targetLanguage)
                : this.requestService.translateText(text, token, 'en');
            return observer as Observable<TranslationModel>;
        } else {
            const defaultTranslation: TranslationModel = {
                detectedLanguage: {
                    language: 'n',
                    confidence: 0
                },
                translatedText: ''
            };
            return of(defaultTranslation);
        }
    }
}