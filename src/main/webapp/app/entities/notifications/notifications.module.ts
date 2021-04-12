import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DuAnTotNghiepBackEndSharedModule } from 'app/shared/shared.module';
import { NotificationsComponent } from './notifications.component';
import { NotificationsDetailComponent } from './notifications-detail.component';
import { NotificationsUpdateComponent } from './notifications-update.component';
import { NotificationsDeleteDialogComponent } from './notifications-delete-dialog.component';
import { notificationsRoute } from './notifications.route';

@NgModule({
  imports: [DuAnTotNghiepBackEndSharedModule, RouterModule.forChild(notificationsRoute)],
  declarations: [NotificationsComponent, NotificationsDetailComponent, NotificationsUpdateComponent, NotificationsDeleteDialogComponent],
  entryComponents: [NotificationsDeleteDialogComponent],
})
export class DuAnTotNghiepBackEndNotificationsModule {}
