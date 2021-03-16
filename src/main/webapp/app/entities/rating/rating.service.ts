import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IRating } from 'app/shared/model/rating.model';

type EntityResponseType = HttpResponse<IRating>;
type EntityArrayResponseType = HttpResponse<IRating[]>;

@Injectable({ providedIn: 'root' })
export class RatingService {
  public resourceUrl = SERVER_API_URL + 'api/ratings';

  constructor(protected http: HttpClient) {}

  create(rating: IRating): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rating);
    return this.http
      .post<IRating>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(rating: IRating): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rating);
    return this.http
      .put<IRating>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IRating>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRating[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(rating: IRating): IRating {
    const copy: IRating = Object.assign({}, rating, {
      times: rating.times && rating.times.isValid() ? rating.times.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.times = res.body.times ? moment(res.body.times) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((rating: IRating) => {
        rating.times = rating.times ? moment(rating.times) : undefined;
      });
    }
    return res;
  }
}
