import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { DuAnTotNghiepBackEndTestModule } from '../../../test.module';
import { ImagesUpdateComponent } from 'app/entities/images/images-update.component';
import { ImagesService } from 'app/entities/images/images.service';
import { Images } from 'app/shared/model/images.model';

describe('Component Tests', () => {
  describe('Images Management Update Component', () => {
    let comp: ImagesUpdateComponent;
    let fixture: ComponentFixture<ImagesUpdateComponent>;
    let service: ImagesService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DuAnTotNghiepBackEndTestModule],
        declarations: [ImagesUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ImagesUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ImagesUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ImagesService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Images(123);
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
        const entity = new Images();
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
