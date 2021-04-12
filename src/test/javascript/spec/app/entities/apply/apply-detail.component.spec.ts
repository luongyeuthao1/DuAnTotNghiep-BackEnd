import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DuAnTotNghiepBackEndTestModule } from '../../../test.module';
import { ApplyDetailComponent } from 'app/entities/apply/apply-detail.component';
import { Apply } from 'app/shared/model/apply.model';

describe('Component Tests', () => {
  describe('Apply Management Detail Component', () => {
    let comp: ApplyDetailComponent;
    let fixture: ComponentFixture<ApplyDetailComponent>;
    const route = ({ data: of({ apply: new Apply(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DuAnTotNghiepBackEndTestModule],
        declarations: [ApplyDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ApplyDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ApplyDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load apply on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.apply).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
