import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IRating, Rating } from 'app/shared/model/rating.model';
import { RatingService } from './rating.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-rating-update',
  templateUrl: './rating-update.component.html',
})
export class RatingUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    content: [],
    times: [],
    rank: [],
    user: [],
  });

  constructor(
    protected ratingService: RatingService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rating }) => {
      if (!rating.id) {
        const today = moment().startOf('day');
        rating.times = today;
      }

      this.updateForm(rating);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(rating: IRating): void {
    this.editForm.patchValue({
      id: rating.id,
      content: rating.content,
      times: rating.times ? rating.times.format(DATE_TIME_FORMAT) : null,
      rank: rating.rank,
      user: rating.user,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rating = this.createFromForm();
    if (rating.id !== undefined) {
      this.subscribeToSaveResponse(this.ratingService.update(rating));
    } else {
      this.subscribeToSaveResponse(this.ratingService.create(rating));
    }
  }

  private createFromForm(): IRating {
    return {
      ...new Rating(),
      id: this.editForm.get(['id'])!.value,
      content: this.editForm.get(['content'])!.value,
      times: this.editForm.get(['times'])!.value ? moment(this.editForm.get(['times'])!.value, DATE_TIME_FORMAT) : undefined,
      rank: this.editForm.get(['rank'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRating>>): void {
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
