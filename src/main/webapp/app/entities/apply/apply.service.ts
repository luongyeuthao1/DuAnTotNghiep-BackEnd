import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IApply } from 'app/shared/model/apply.model';

type EntityResponseType = HttpResponse<IApply>;
type EntityArrayResponseType = HttpResponse<IApply[]>;

@Injectable({ providedIn: 'root' })
export class ApplyService {
  public resourceUrl = SERVER_API_URL + 'api/applies';

  constructor(protected http: HttpClient) {}

  create(apply: IApply): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(apply);
    return this.http
      .post<IApply>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(apply: IApply): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(apply);
    return this.http
      .put<IApply>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IApply>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IApply[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(apply: IApply): IApply {
    const copy: IApply = Object.assign({}, apply, {
      time: apply.time && apply.time.isValid() ? apply.time.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.time = res.body.time ? moment(res.body.time) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((apply: IApply) => {
        apply.time = apply.time ? moment(apply.time) : undefined;
      });
    }
    return res;
  }
}
