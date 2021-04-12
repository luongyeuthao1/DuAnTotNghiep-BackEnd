import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DuAnTotNghiepBackEndSharedModule } from 'app/shared/shared.module';
import { ApplyComponent } from './apply.component';
import { ApplyDetailComponent } from './apply-detail.component';
import { ApplyUpdateComponent } from './apply-update.component';
import { ApplyDeleteDialogComponent } from './apply-delete-dialog.component';
import { applyRoute } from './apply.route';

@NgModule({
  imports: [DuAnTotNghiepBackEndSharedModule, RouterModule.forChild(applyRoute)],
  declarations: [ApplyComponent, ApplyDetailComponent, ApplyUpdateComponent, ApplyDeleteDialogComponent],
  entryComponents: [ApplyDeleteDialogComponent],
})
export class DuAnTotNghiepBackEndApplyModule {}
