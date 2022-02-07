import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ActorDetailComponent } from './actor-detail.component';

describe('Actor Management Detail Component', () => {
  let comp: ActorDetailComponent;
  let fixture: ComponentFixture<ActorDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActorDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ actor: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ActorDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ActorDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load actor on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.actor).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
