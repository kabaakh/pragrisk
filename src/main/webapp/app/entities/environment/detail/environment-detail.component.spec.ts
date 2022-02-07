import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnvironmentDetailComponent } from './environment-detail.component';

describe('Environment Management Detail Component', () => {
  let comp: EnvironmentDetailComponent;
  let fixture: ComponentFixture<EnvironmentDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnvironmentDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ environment: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnvironmentDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnvironmentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load environment on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.environment).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
