import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { INotifications } from 'app/shared/model/notifications.model';
import { NotificationsService } from './notifications.service';

@Component({
  templateUrl: './notifications-delete-dialog.component.html',
})
export class NotificationsDeleteDialogComponent {
  notifications?: INotifications;

  constructor(
    protected notificationsService: NotificationsService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.notificationsService.delete(id).subscribe(() => {
      this.eventManager.broadcast('notificationsListModification');
      this.activeModal.close();
    });
  }
}
