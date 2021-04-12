import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface INotifications {
  id?: number;
  content?: string;
  times?: Moment;
  status?: boolean;
  user?: IUser;
}

export class Notifications implements INotifications {
  constructor(public id?: number, public content?: string, public times?: Moment, public status?: boolean, public user?: IUser) {
    this.status = this.status || false;
  }
}
