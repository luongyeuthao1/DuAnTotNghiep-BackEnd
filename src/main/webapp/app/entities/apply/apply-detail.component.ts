import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IApply } from 'app/shared/model/apply.model';

@Component({
  selector: 'jhi-apply-detail',
  templateUrl: './apply-detail.component.html',
})
export class ApplyDetailComponent implements OnInit {
  apply: IApply | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apply }) => (this.apply = apply));
  }

  previousState(): void {
    window.history.back();
  }
}
