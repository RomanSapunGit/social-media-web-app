import {Timestamp} from "rxjs";
import {FileDTO} from "./file.model";
import {CommentModel} from "./comment.model";

export interface PostModel {
  identifier: string;
  title: string;
  description: string;
  creationTime: Timestamp<any>;
  username: string;
  userImage: FileDTO;
  postImages: FileDTO[];

}
