import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IImages, Images } from 'app/shared/model/images.model';
import { ImagesService } from './images.service';
import { ImagesComponent } from './images.component';
import { ImagesDetailComponent } from './images-detail.component';
import { ImagesUpdateComponent } from './images-update.component';

@Injectable({ providedIn: 'root' })
export class ImagesResolve implements Resolve<IImages> {
  constructor(private service: ImagesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IImages> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((images: HttpResponse<Images>) => {
          if (images.body) {
            return of(images.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Images());
  }
}

export const imagesRoute: Routes = [
  {
    path: '',
    component: ImagesComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.images.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ImagesDetailComponent,
    resolve: {
      images: ImagesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.images.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ImagesUpdateComponent,
    resolve: {
      images: ImagesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.images.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ImagesUpdateComponent,
    resolve: {
      images: ImagesResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.images.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
