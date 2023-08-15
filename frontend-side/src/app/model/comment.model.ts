import {Timestamp} from "rxjs";
import {FileDTO} from "./file.model";

export interface CommentModel {
  identifier: string
  title: string
  description: string
  username: string
  creationTime: Timestamp<any>
  userImage: FileDTO;
}
