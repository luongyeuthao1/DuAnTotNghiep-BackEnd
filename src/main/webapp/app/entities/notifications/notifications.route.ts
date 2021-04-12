import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { INotifications, Notifications } from 'app/shared/model/notifications.model';
import { NotificationsService } from './notifications.service';
import { NotificationsComponent } from './notifications.component';
import { NotificationsDetailComponent } from './notifications-detail.component';
import { NotificationsUpdateComponent } from './notifications-update.component';

@Injectable({ providedIn: 'root' })
export class NotificationsResolve implements Resolve<INotifications> {
  constructor(private service: NotificationsService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INotifications> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((notifications: HttpResponse<Notifications>) => {
          if (notifications.body) {
            return of(notifications.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Notifications());
  }
}

export const notificationsRoute: Routes = [
  {
    path: '',
    component: NotificationsComponent,
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'duAnTotNghiepBackEndApp.notifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotificationsDetailComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.notifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotificationsUpdateComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.notifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotificationsUpdateComponent,
    resolve: {
      notifications: NotificationsResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'duAnTotNghiepBackEndApp.notifications.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
