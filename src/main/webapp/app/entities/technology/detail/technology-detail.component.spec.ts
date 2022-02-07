import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TechnologyDetailComponent } from './technology-detail.component';

describe('Technology Management Detail Component', () => {
  let comp: TechnologyDetailComponent;
  let fixture: ComponentFixture<TechnologyDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TechnologyDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ technology: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TechnologyDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TechnologyDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load technology on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.technology).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
