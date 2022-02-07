import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ActorService } from '../service/actor.service';
import { IActor, Actor } from '../actor.model';
import { IEnvironment } from 'app/entities/environment/environment.model';
import { EnvironmentService } from 'app/entities/environment/service/environment.service';

import { ActorUpdateComponent } from './actor-update.component';

describe('Actor Management Update Component', () => {
  let comp: ActorUpdateComponent;
  let fixture: ComponentFixture<ActorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let actorService: ActorService;
  let environmentService: EnvironmentService;

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
    environmentService = TestBed.inject(EnvironmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call parentActor query and add missing value', () => {
      const actor: IActor = { id: 456 };
      const parentActor: IActor = { id: 75493 };
      actor.parentActor = parentActor;

      const parentActorCollection: IActor[] = [{ id: 26003 }];
      jest.spyOn(actorService, 'query').mockReturnValue(of(new HttpResponse({ body: parentActorCollection })));
      const expectedCollection: IActor[] = [parentActor, ...parentActorCollection];
      jest.spyOn(actorService, 'addActorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      expect(actorService.query).toHaveBeenCalled();
      expect(actorService.addActorToCollectionIfMissing).toHaveBeenCalledWith(parentActorCollection, parentActor);
      expect(comp.parentActorsCollection).toEqual(expectedCollection);
    });

    it('Should call Environment query and add missing value', () => {
      const actor: IActor = { id: 456 };
      const group: IEnvironment = { id: 96092 };
      actor.group = group;

      const environmentCollection: IEnvironment[] = [{ id: 56263 }];
      jest.spyOn(environmentService, 'query').mockReturnValue(of(new HttpResponse({ body: environmentCollection })));
      const additionalEnvironments = [group];
      const expectedCollection: IEnvironment[] = [...additionalEnvironments, ...environmentCollection];
      jest.spyOn(environmentService, 'addEnvironmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      expect(environmentService.query).toHaveBeenCalled();
      expect(environmentService.addEnvironmentToCollectionIfMissing).toHaveBeenCalledWith(environmentCollection, ...additionalEnvironments);
      expect(comp.environmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const actor: IActor = { id: 456 };
      const parentActor: IActor = { id: 52234 };
      actor.parentActor = parentActor;
      const group: IEnvironment = { id: 38733 };
      actor.group = group;

      activatedRoute.data = of({ actor });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(actor));
      expect(comp.parentActorsCollection).toContain(parentActor);
      expect(comp.environmentsSharedCollection).toContain(group);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Actor>>();
      const actor = { id: 123 };
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
      const actor = { id: 123 };
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
    describe('trackActorById', () => {
      it('Should return tracked Actor primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackActorById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackEnvironmentById', () => {
      it('Should return tracked Environment primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackEnvironmentById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
