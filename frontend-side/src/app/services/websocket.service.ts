import {Injectable} from "@angular/core";
import { Client } from '@stomp/stompjs';
import {CommentModel} from "../model/comment.model";
import {RequestService} from "./request.service";
import {environment} from "../../environments/environment";
@Injectable({
    providedIn: 'root'
})
export class WebsocketService {
    private stompClient: Client;

    constructor(private requestService: RequestService) {
        this.stompClient = new Client({
            brokerURL: `ws://${environment.backendUrl}/ws`,
            connectHeaders: {
                'X-CSRF-TOKEN': requestService.getCsrfToken().token
            },
            debug: (str) => {
                console.log(str);
            }
        });
    }

    connect() {
        this.stompClient.activate();
    }

    subscribe() {
        this.stompClient.onConnect = (frame) => {
            this.stompClient.subscribe('/topic/comments', (comment) => {
                console.log(JSON.parse(comment.body));
            });
        };
    }

    publish(comment: CommentModel) {
        // Convert Uint8Array to Base64-encoded string
        const commentToSend = {
            identifier: comment.identifier,
            title: comment.title,
            description: comment.description,
            username: comment.username,
            creationTime: comment.creationTime,
            postAuthorUsername: comment.postAuthorUsername
        };

        this.stompClient.publish({
            destination: '/app/ws',
            body: JSON.stringify(commentToSend)
        });
    }
    disconnect() {
        this.stompClient.deactivate();
    }
}