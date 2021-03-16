import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { DuAnTotNghiepBackEndTestModule } from '../../../test.module';
import { RatingUpdateComponent } from 'app/entities/rating/rating-update.component';
import { RatingService } from 'app/entities/rating/rating.service';
import { Rating } from 'app/shared/model/rating.model';

describe('Component Tests', () => {
  describe('Rating Management Update Component', () => {
    let comp: RatingUpdateComponent;
    let fixture: ComponentFixture<RatingUpdateComponent>;
    let service: RatingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DuAnTotNghiepBackEndTestModule],
        declarations: [RatingUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(RatingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RatingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RatingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Rating(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Rating();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
