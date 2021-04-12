import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IImages } from 'app/shared/model/images.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ImagesService } from './images.service';
import { ImagesDeleteDialogComponent } from './images-delete-dialog.component';

@Component({
  selector: 'jhi-images',
  templateUrl: './images.component.html',
})
export class ImagesComponent implements OnInit, OnDestroy {
  images: IImages[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected imagesService: ImagesService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.images = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.imagesService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe((res: HttpResponse<IImages[]>) => this.paginateImages(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.images = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInImages();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IImages): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInImages(): void {
    this.eventSubscriber = this.eventManager.subscribe('imagesListModification', () => this.reset());
  }

  delete(images: IImages): void {
    const modalRef = this.modalService.open(ImagesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.images = images;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateImages(data: IImages[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.images.push(data[i]);
      }
    }
  }
}
