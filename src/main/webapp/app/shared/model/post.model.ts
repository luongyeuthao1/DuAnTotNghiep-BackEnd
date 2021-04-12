import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IPost {
  id?: number;
  content?: string;
  createTime?: Moment;
  like?: number;
  type?: number;
  user?: IUser;
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public content?: string,
    public createTime?: Moment,
    public like?: number,
    public type?: number,
    public user?: IUser
  ) {}
}
