import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IImages } from 'app/shared/model/images.model';

@Component({
  selector: 'jhi-images-detail',
  templateUrl: './images-detail.component.html',
})
export class ImagesDetailComponent implements OnInit {
  images: IImages | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ images }) => (this.images = images));
  }

  previousState(): void {
    window.history.back();
  }
}
