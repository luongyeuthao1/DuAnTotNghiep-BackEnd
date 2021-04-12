import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPost, Post } from 'app/shared/model/post.model';
import { PostService } from './post.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-post-update',
  templateUrl: './post-update.component.html',
})
export class PostUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    content: [],
    createTime: [],
    like: [],
    type: [],
    user: [],
  });

  constructor(
    protected postService: PostService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      if (!post.id) {
        const today = moment().startOf('day');
        post.createTime = today;
      }

      this.updateForm(post);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(post: IPost): void {
    this.editForm.patchValue({
      id: post.id,
      content: post.content,
      createTime: post.createTime ? post.createTime.format(DATE_TIME_FORMAT) : null,
      like: post.like,
      type: post.type,
      user: post.user,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const post = this.createFromForm();
    if (post.id !== undefined) {
      this.subscribeToSaveResponse(this.postService.update(post));
    } else {
      this.subscribeToSaveResponse(this.postService.create(post));
    }
  }

  private createFromForm(): IPost {
    return {
      ...new Post(),
      id: this.editForm.get(['id'])!.value,
      content: this.editForm.get(['content'])!.value,
      createTime: this.editForm.get(['createTime'])!.value ? moment(this.editForm.get(['createTime'])!.value, DATE_TIME_FORMAT) : undefined,
      like: this.editForm.get(['like'])!.value,
      type: this.editForm.get(['type'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPost>>): void {
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

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
