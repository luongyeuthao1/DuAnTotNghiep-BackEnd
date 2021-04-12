import { IPost } from 'app/shared/model/post.model';

export interface IImages {
  id?: number;
  url?: string;
  post?: IPost;
}

export class Images implements IImages {
  constructor(public id?: number, public url?: string, public post?: IPost) {}
}
