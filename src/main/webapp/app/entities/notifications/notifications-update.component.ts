import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { INotifications, Notifications } from 'app/shared/model/notifications.model';
import { NotificationsService } from './notifications.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-notifications-update',
  templateUrl: './notifications-update.component.html',
})
export class NotificationsUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    content: [],
    times: [],
    status: [],
    user: [],
  });

  constructor(
    protected notificationsService: NotificationsService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notifications }) => {
      if (!notifications.id) {
        const today = moment().startOf('day');
        notifications.times = today;
      }

      this.updateForm(notifications);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(notifications: INotifications): void {
    this.editForm.patchValue({
      id: notifications.id,
      content: notifications.content,
      times: notifications.times ? notifications.times.format(DATE_TIME_FORMAT) : null,
      status: notifications.status,
      user: notifications.user,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notifications = this.createFromForm();
    if (notifications.id !== undefined) {
      this.subscribeToSaveResponse(this.notificationsService.update(notifications));
    } else {
      this.subscribeToSaveResponse(this.notificationsService.create(notifications));
    }
  }

  private createFromForm(): INotifications {
    return {
      ...new Notifications(),
      id: this.editForm.get(['id'])!.value,
      content: this.editForm.get(['content'])!.value,
      times: this.editForm.get(['times'])!.value ? moment(this.editForm.get(['times'])!.value, DATE_TIME_FORMAT) : undefined,
      status: this.editForm.get(['status'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotifications>>): void {
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
