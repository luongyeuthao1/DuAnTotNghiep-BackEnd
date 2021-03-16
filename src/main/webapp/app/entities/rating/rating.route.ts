import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IRating, Rating } from 'app/shared/model/rating.model';
import { RatingService } from './rating.service';
import { RatingComponent } from './rating.component';
import { RatingDetailComponent } from './rating-detail.component';
import { RatingUpdateComponent } from './rating-update.component';

@Injectable({ providedIn: 'root' })
export class RatingResolve implements Resolve<IRating> {
  constructor(private service: RatingService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRating> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((rating: HttpResponse<Rating>) => {
          if (rating.body) {
            return of(rating.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Rating());
  }
}

export const ratingRoute: Routes = [
  {
    path: '',
    component: RatingComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'duAnTotNghiepBackEndApp.rating.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RatingDetailComponent,
    resolve: {
      rating: RatingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.rating.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RatingUpdateComponent,
    resolve: {
      rating: RatingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.rating.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RatingUpdateComponent,
    resolve: {
      rating: RatingResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.rating.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
