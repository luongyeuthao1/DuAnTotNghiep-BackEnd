import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IApply, Apply } from 'app/shared/model/apply.model';
import { ApplyService } from './apply.service';
import { ApplyComponent } from './apply.component';
import { ApplyDetailComponent } from './apply-detail.component';
import { ApplyUpdateComponent } from './apply-update.component';

@Injectable({ providedIn: 'root' })
export class ApplyResolve implements Resolve<IApply> {
  constructor(private service: ApplyService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IApply> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((apply: HttpResponse<Apply>) => {
          if (apply.body) {
            return of(apply.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Apply());
  }
}

export const applyRoute: Routes = [
  {
    path: '',
    component: ApplyComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'duAnTotNghiepBackEndApp.apply.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ApplyDetailComponent,
    resolve: {
      apply: ApplyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.apply.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ApplyUpdateComponent,
    resolve: {
      apply: ApplyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.apply.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ApplyUpdateComponent,
    resolve: {
      apply: ApplyResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.apply.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
