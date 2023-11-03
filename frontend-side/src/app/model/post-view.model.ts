import {Timestamp} from "rxjs";
import {FileDTO} from "./file.model";
import {Page} from "./page.model";
import {UserModel} from "./user.model";

export interface PostViewModel {
  identifier: string;
  title: string;
  description: string;
  creationTime: Timestamp<any>;
  username: string;
  userImage: FileDTO;
  postImages: FileDTO[];
  commentsPage?: Page;
  upvotes: UserModel[];
  downvotes: UserModel[];
}
