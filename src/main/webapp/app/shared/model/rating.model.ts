import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IRating {
  id?: number;
  content?: string;
  times?: Moment;
  rank?: number;
  user?: IUser;
}

export class Rating implements IRating {
  constructor(public id?: number, public content?: string, public times?: Moment, public rank?: number, public user?: IUser) {}
}
