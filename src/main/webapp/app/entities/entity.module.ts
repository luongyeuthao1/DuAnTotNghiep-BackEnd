import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'rating',
        loadChildren: () => import('./rating/rating.module').then(m => m.DuAnTotNghiepBackEndRatingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class DuAnTotNghiepBackEndEntityModule {}
