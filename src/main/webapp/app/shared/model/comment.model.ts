import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model/post.model';

export interface IComment {
  id?: number;
  time?: Moment;
  content?: string;
  user?: IUser;
  post?: IPost;
}

export class Comment implements IComment {
  constructor(public id?: number, public time?: Moment, public content?: string, public user?: IUser, public post?: IPost) {}
}
