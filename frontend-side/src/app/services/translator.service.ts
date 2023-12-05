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
        let targetLanguage = localStorage.getItem("Language") == 'ua' ? 'uk' : localStorage.getItem("Language");
        console.log('check');
            let observer = targetLanguage ? this.requestService.translateText(text, targetLanguage)
                : this.requestService.translateText(text, 'en');
            return observer as Observable<TranslationModel>;
    }
}