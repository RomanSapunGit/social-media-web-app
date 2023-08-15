import {Timestamp} from "rxjs";
import {FileDTO} from "./file.model";
import {CommentModel} from "./comment.model";
import {Page} from "./page.model";

export interface PostViewModel {
  identifier: string;
  title: string;
  description: string;
  creationTime: Timestamp<any>;
  username: string;
  userImage: FileDTO;
  postImages: FileDTO[];
  commentsPage: Page;
}
