import {Injectable} from "@angular/core";
import {ReplaySubject} from "rxjs";
import {CommentModel} from "../model/comment.model";
import {PostModel} from "../model/post.model";

@Injectable({
  providedIn: 'root'
})
export class PostActionService {
  private postCreated: ReplaySubject<any> = new ReplaySubject<any>();
  get postCreated$() {
    return this.postCreated;
  }

  addPost(postModel: PostModel) {
    this.postCreated.next(postModel);
  }
}
