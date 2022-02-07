import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MitigationDetailComponent } from './mitigation-detail.component';

describe('Mitigation Management Detail Component', () => {
  let comp: MitigationDetailComponent;
  let fixture: ComponentFixture<MitigationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MitigationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ mitigation: { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' } }) },
        },
      ],
    })
      .overrideTemplate(MitigationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MitigationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load mitigation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.mitigation).toEqual(expect.objectContaining({ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' }));
    });
  });
});
