import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { RatingService } from 'app/entities/rating/rating.service';
import { IRating, Rating } from 'app/shared/model/rating.model';

describe('Service Tests', () => {
  describe('Rating Service', () => {
    let injector: TestBed;
    let service: RatingService;
    let httpMock: HttpTestingController;
    let elemDefault: IRating;
    let expectedResult: IRating | IRating[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(RatingService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Rating(0, 'AAAAAAA', currentDate, 0);
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

      it('should create a Rating', () => {
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

        service.create(new Rating()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Rating', () => {
        const returnedFromService = Object.assign(
          {
            content: 'BBBBBB',
            times: currentDate.format(DATE_TIME_FORMAT),
            rank: 1,
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

      it('should return a list of Rating', () => {
        const returnedFromService = Object.assign(
          {
            content: 'BBBBBB',
            times: currentDate.format(DATE_TIME_FORMAT),
            rank: 1,
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

      it('should delete a Rating', () => {
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
