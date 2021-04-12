import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IComment } from 'app/shared/model/comment.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { CommentService } from './comment.service';
import { CommentDeleteDialogComponent } from './comment-delete-dialog.component';

@Component({
  selector: 'jhi-comment',
  templateUrl: './comment.component.html',
})
export class CommentComponent implements OnInit, OnDestroy {
  comments: IComment[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected commentService: CommentService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.comments = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.commentService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe((res: HttpResponse<IComment[]>) => this.paginateComments(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.comments = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInComments();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IComment): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInComments(): void {
    this.eventSubscriber = this.eventManager.subscribe('commentListModification', () => this.reset());
  }

  delete(comment: IComment): void {
    const modalRef = this.modalService.open(CommentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.comment = comment;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateComments(data: IComment[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.comments.push(data[i]);
      }
    }
  }
}
