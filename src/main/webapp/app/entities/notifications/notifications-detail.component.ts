import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INotifications } from 'app/shared/model/notifications.model';

@Component({
  selector: 'jhi-notifications-detail',
  templateUrl: './notifications-detail.component.html',
})
export class NotificationsDetailComponent implements OnInit {
  notifications: INotifications | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notifications }) => (this.notifications = notifications));
  }

  previousState(): void {
    window.history.back();
  }
}
