import {Timestamp} from "rxjs";

export interface PostModel {
  identifier: string
  title: string
  description: string
  creationTime: Timestamp<any>
  username: string
}
