import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ActorService } from '../service/actor.service';
import { IActor, Actor } from '../actor.model';

import { ActorUpdateComponent } from './actor-update.component';

describe('Actor Management Update Component', () => {
  let comp: ActorUpdateComponent;
  let fixture: ComponentFixture<ActorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let actorService: ActorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ActorUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ActorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    actorService = TestBed.inject(ActorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call inheritsFrom query and add missing value', () => {
      const actor: IActor = { actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const inheritsFrom: IActor = { actorID: 'c48d3c12-6491-486a-8d3e-5773d0352521' };
      actor.inheritsFrom = inheritsFrom;

      const inheritsFromCollection: IActor[] = [{ actorID: 'ac98cd46-018e-47ec-8793-ac5589028252' }];
      jest.spyOn(actorService, 'query').mockReturnValue(of(new HttpResponse({ body: inheritsFromCollection })));
      const expectedCollection: IActor[] = [inheritsFrom, ...inheritsFromCollection];
      jest.spyOn(actorService, 'addActorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      expect(actorService.query).toHaveBeenCalled();
      expect(actorService.addActorToCollectionIfMissing).toHaveBeenCalledWith(inheritsFromCollection, inheritsFrom);
      expect(comp.inheritsFromsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const actor: IActor = { actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const inheritsFrom: IActor = { actorID: '00935c91-14da-48b1-894e-9f59675a9637' };
      actor.inheritsFrom = inheritsFrom;

      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(actor));
      expect(comp.inheritsFromsCollection).toContain(inheritsFrom);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Actor>>();
      const actor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(actorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: actor }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(actorService.update).toHaveBeenCalledWith(actor);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Actor>>();
      const actor = new Actor();
      jest.spyOn(actorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: actor }));
      saveSubject.complete();

      // THEN
      expect(actorService.create).toHaveBeenCalledWith(actor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Actor>>();
      const actor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(actorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(actorService.update).toHaveBeenCalledWith(actor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackActorByActorID', () => {
      it('Should return tracked Actor primary key', () => {
        const entity = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const trackResult = comp.trackActorByActorID(0, entity);
        expect(trackResult).toEqual(entity.actorID);
      });
    });
  });
});