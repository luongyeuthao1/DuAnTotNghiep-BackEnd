import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IImages } from 'app/shared/model/images.model';
import { ImagesService } from './images.service';

@Component({
  templateUrl: './images-delete-dialog.component.html',
})
export class ImagesDeleteDialogComponent {
  images?: IImages;

  constructor(protected imagesService: ImagesService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.imagesService.delete(id).subscribe(() => {
      this.eventManager.broadcast('imagesListModification');
      this.activeModal.close();
    });
  }
}
