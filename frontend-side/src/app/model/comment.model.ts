import {Timestamp} from "rxjs";

export interface CommentModel {
  identifier: string
  title: string
  description: string
  username: string
  creationTime: Timestamp<any>
}
