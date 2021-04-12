import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DuAnTotNghiepBackEndTestModule } from '../../../test.module';
import { ImagesDetailComponent } from 'app/entities/images/images-detail.component';
import { Images } from 'app/shared/model/images.model';

describe('Component Tests', () => {
  describe('Images Management Detail Component', () => {
    let comp: ImagesDetailComponent;
    let fixture: ComponentFixture<ImagesDetailComponent>;
    const route = ({ data: of({ images: new Images(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DuAnTotNghiepBackEndTestModule],
        declarations: [ImagesDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ImagesDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ImagesDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load images on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.images).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
