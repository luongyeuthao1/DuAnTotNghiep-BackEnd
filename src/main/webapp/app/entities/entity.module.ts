import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'rating',
        loadChildren: () => import('./rating/rating.module').then(m => m.DuAnTotNghiepBackEndRatingModule),
      },
      {
        path: 'post',
        loadChildren: () => import('./post/post.module').then(m => m.DuAnTotNghiepBackEndPostModule),
      },
      {
        path: 'notifications',
        loadChildren: () => import('./notifications/notifications.module').then(m => m.DuAnTotNghiepBackEndNotificationsModule),
      },
      {
        path: 'apply',
        loadChildren: () => import('./apply/apply.module').then(m => m.DuAnTotNghiepBackEndApplyModule),
      },
      {
        path: 'comment',
        loadChildren: () => import('./comment/comment.module').then(m => m.DuAnTotNghiepBackEndCommentModule),
      },
      {
        path: 'images',
        loadChildren: () => import('./images/images.module').then(m => m.DuAnTotNghiepBackEndImagesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class DuAnTotNghiepBackEndEntityModule {}
