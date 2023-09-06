import {Injectable} from "@angular/core";
import {RequestService} from "./request.service";
import {Observable, of} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class SubscriptionService {
    constructor(private requestService: RequestService) {
    }
    addSubscription(username: string, token: string | null): Observable<any> {
        return this.requestService.addFollowing(token, username);
    }
    removeSubscription(username: string, token: string | null): Observable<any> {
        return this.requestService.removeFollowing(token, username);
    }
    findFollowingByUsername(token: string | null, username: string): Observable<any> {
        return this.requestService.findFollowingByUsername(token, username);
    }
}