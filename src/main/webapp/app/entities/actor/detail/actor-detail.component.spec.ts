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
          useValue: { data: of({ actor: { actorID: '9fec3727-3421-4967-b213-ba36557ca194' } }) },
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
      expect(comp.actor).toEqual(expect.objectContaining({ actorID: '9fec3727-3421-4967-b213-ba36557ca194' }));
    });
  });
});
