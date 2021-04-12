import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IApply } from 'app/shared/model/apply.model';
import { ApplyService } from './apply.service';

@Component({
  templateUrl: './apply-delete-dialog.component.html',
})
export class ApplyDeleteDialogComponent {
  apply?: IApply;

  constructor(protected applyService: ApplyService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.applyService.delete(id).subscribe(() => {
      this.eventManager.broadcast('applyListModification');
      this.activeModal.close();
    });
  }
}
