import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IImages, Images } from 'app/shared/model/images.model';
import { ImagesService } from './images.service';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';

@Component({
  selector: 'jhi-images-update',
  templateUrl: './images-update.component.html',
})
export class ImagesUpdateComponent implements OnInit {
  isSaving = false;
  posts: IPost[] = [];

  editForm = this.fb.group({
    id: [],
    url: [],
    post: [],
  });

  constructor(
    protected imagesService: ImagesService,
    protected postService: PostService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ images }) => {
      this.updateForm(images);

      this.postService.query().subscribe((res: HttpResponse<IPost[]>) => (this.posts = res.body || []));
    });
  }

  updateForm(images: IImages): void {
    this.editForm.patchValue({
      id: images.id,
      url: images.url,
      post: images.post,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const images = this.createFromForm();
    if (images.id !== undefined) {
      this.subscribeToSaveResponse(this.imagesService.update(images));
    } else {
      this.subscribeToSaveResponse(this.imagesService.create(images));
    }
  }

  private createFromForm(): IImages {
    return {
      ...new Images(),
      id: this.editForm.get(['id'])!.value,
      url: this.editForm.get(['url'])!.value,
      post: this.editForm.get(['post'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IImages>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IPost): any {
    return item.id;
  }
}
