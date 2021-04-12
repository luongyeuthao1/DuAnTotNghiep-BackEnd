import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IApply, Apply } from 'app/shared/model/apply.model';
import { ApplyService } from './apply.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';

type SelectableEntity = IUser | IPost;

@Component({
  selector: 'jhi-apply-update',
  templateUrl: './apply-update.component.html',
})
export class ApplyUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  posts: IPost[] = [];

  editForm = this.fb.group({
    id: [],
    time: [],
    content: [],
    user: [],
    post: [],
  });

  constructor(
    protected applyService: ApplyService,
    protected userService: UserService,
    protected postService: PostService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apply }) => {
      if (!apply.id) {
        const today = moment().startOf('day');
        apply.time = today;
      }

      this.updateForm(apply);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.postService.query().subscribe((res: HttpResponse<IPost[]>) => (this.posts = res.body || []));
    });
  }

  updateForm(apply: IApply): void {
    this.editForm.patchValue({
      id: apply.id,
      time: apply.time ? apply.time.format(DATE_TIME_FORMAT) : null,
      content: apply.content,
      user: apply.user,
      post: apply.post,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const apply = this.createFromForm();
    if (apply.id !== undefined) {
      this.subscribeToSaveResponse(this.applyService.update(apply));
    } else {
      this.subscribeToSaveResponse(this.applyService.create(apply));
    }
  }

  private createFromForm(): IApply {
    return {
      ...new Apply(),
      id: this.editForm.get(['id'])!.value,
      time: this.editForm.get(['time'])!.value ? moment(this.editForm.get(['time'])!.value, DATE_TIME_FORMAT) : undefined,
      content: this.editForm.get(['content'])!.value,
      user: this.editForm.get(['user'])!.value,
      post: this.editForm.get(['post'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApply>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
