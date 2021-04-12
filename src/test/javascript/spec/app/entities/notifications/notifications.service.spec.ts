import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { NotificationsService } from 'app/entities/notifications/notifications.service';
import { INotifications, Notifications } from 'app/shared/model/notifications.model';

describe('Service Tests', () => {
  describe('Notifications Service', () => {
    let injector: TestBed;
    let service: NotificationsService;
    let httpMock: HttpTestingController;
    let elemDefault: INotifications;
    let expectedResult: INotifications | INotifications[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(NotificationsService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Notifications(0, 'AAAAAAA', currentDate, false);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            times: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Notifications', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            times: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            times: currentDate,
          },
          returnedFromService
        );

        service.create(new Notifications()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Notifications', () => {
        const returnedFromService = Object.assign(
          {
            content: 'BBBBBB',
            times: currentDate.format(DATE_TIME_FORMAT),
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            times: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Notifications', () => {
        const returnedFromService = Object.assign(
          {
            content: 'BBBBBB',
            times: currentDate.format(DATE_TIME_FORMAT),
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            times: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Notifications', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
