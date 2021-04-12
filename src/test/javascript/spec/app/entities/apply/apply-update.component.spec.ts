import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { DuAnTotNghiepBackEndTestModule } from '../../../test.module';
import { ApplyUpdateComponent } from 'app/entities/apply/apply-update.component';
import { ApplyService } from 'app/entities/apply/apply.service';
import { Apply } from 'app/shared/model/apply.model';

describe('Component Tests', () => {
  describe('Apply Management Update Component', () => {
    let comp: ApplyUpdateComponent;
    let fixture: ComponentFixture<ApplyUpdateComponent>;
    let service: ApplyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DuAnTotNghiepBackEndTestModule],
        declarations: [ApplyUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ApplyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApplyUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ApplyService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Apply(123);
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
        const entity = new Apply();
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
