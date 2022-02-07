import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ScenarioService } from '../service/scenario.service';
import { IScenario, Scenario } from '../scenario.model';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';
import { ITechnology } from 'app/entities/technology/technology.model';
import { TechnologyService } from 'app/entities/technology/service/technology.service';
import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';
import { VulnerabilityService } from 'app/entities/vulnerability/service/vulnerability.service';

import { ScenarioUpdateComponent } from './scenario-update.component';

describe('Scenario Management Update Component', () => {
  let comp: ScenarioUpdateComponent;
  let fixture: ComponentFixture<ScenarioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let scenarioService: ScenarioService;
  let actorService: ActorService;
  let technologyService: TechnologyService;
  let vulnerabilityService: VulnerabilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ScenarioUpdateComponent],
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
      .overrideTemplate(ScenarioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    scenarioService = TestBed.inject(ScenarioService);
    actorService = TestBed.inject(ActorService);
    technologyService = TestBed.inject(TechnologyService);
    vulnerabilityService = TestBed.inject(VulnerabilityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Actor query and add missing value', () => {
      const scenario: IScenario = { id: 456 };
      const actorFK: IActor = { id: 7828 };
      scenario.actorFK = actorFK;

      const actorCollection: IActor[] = [{ id: 76094 }];
      jest.spyOn(actorService, 'query').mockReturnValue(of(new HttpResponse({ body: actorCollection })));
      const additionalActors = [actorFK];
      const expectedCollection: IActor[] = [...additionalActors, ...actorCollection];
      jest.spyOn(actorService, 'addActorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(actorService.query).toHaveBeenCalled();
      expect(actorService.addActorToCollectionIfMissing).toHaveBeenCalledWith(actorCollection, ...additionalActors);
      expect(comp.actorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Technology query and add missing value', () => {
      const scenario: IScenario = { id: 456 };
      const technologyFK: ITechnology = { id: 36196 };
      scenario.technologyFK = technologyFK;

      const technologyCollection: ITechnology[] = [{ id: 17110 }];
      jest.spyOn(technologyService, 'query').mockReturnValue(of(new HttpResponse({ body: technologyCollection })));
      const additionalTechnologies = [technologyFK];
      const expectedCollection: ITechnology[] = [...additionalTechnologies, ...technologyCollection];
      jest.spyOn(technologyService, 'addTechnologyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(technologyService.query).toHaveBeenCalled();
      expect(technologyService.addTechnologyToCollectionIfMissing).toHaveBeenCalledWith(technologyCollection, ...additionalTechnologies);
      expect(comp.technologiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Vulnerability query and add missing value', () => {
      const scenario: IScenario = { id: 456 };
      const vulnerabilityFK: IVulnerability = { id: 56041 };
      scenario.vulnerabilityFK = vulnerabilityFK;

      const vulnerabilityCollection: IVulnerability[] = [{ id: 12816 }];
      jest.spyOn(vulnerabilityService, 'query').mockReturnValue(of(new HttpResponse({ body: vulnerabilityCollection })));
      const additionalVulnerabilities = [vulnerabilityFK];
      const expectedCollection: IVulnerability[] = [...additionalVulnerabilities, ...vulnerabilityCollection];
      jest.spyOn(vulnerabilityService, 'addVulnerabilityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(vulnerabilityService.query).toHaveBeenCalled();
      expect(vulnerabilityService.addVulnerabilityToCollectionIfMissing).toHaveBeenCalledWith(
        vulnerabilityCollection,
        ...additionalVulnerabilities
      );
      expect(comp.vulnerabilitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const scenario: IScenario = { id: 456 };
      const actorFK: IActor = { id: 92749 };
      scenario.actorFK = actorFK;
      const technologyFK: ITechnology = { id: 19725 };
      scenario.technologyFK = technologyFK;
      const vulnerabilityFK: IVulnerability = { id: 99959 };
      scenario.vulnerabilityFK = vulnerabilityFK;

      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(scenario));
      expect(comp.actorsSharedCollection).toContain(actorFK);
      expect(comp.technologiesSharedCollection).toContain(technologyFK);
      expect(comp.vulnerabilitiesSharedCollection).toContain(vulnerabilityFK);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = { id: 123 };
      jest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scenario }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(scenarioService.update).toHaveBeenCalledWith(scenario);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = new Scenario();
      jest.spyOn(scenarioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: scenario }));
      saveSubject.complete();

      // THEN
      expect(scenarioService.create).toHaveBeenCalledWith(scenario);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Scenario>>();
      const scenario = { id: 123 };
      jest.spyOn(scenarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ scenario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(scenarioService.update).toHaveBeenCalledWith(scenario);
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

    describe('trackTechnologyById', () => {
      it('Should return tracked Technology primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTechnologyById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackVulnerabilityById', () => {
      it('Should return tracked Vulnerability primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackVulnerabilityById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
