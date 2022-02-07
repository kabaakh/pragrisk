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
      const scenario: IScenario = { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const actorFK: IActor = { actorID: '1ce9e107-a500-4f35-ae16-81f9ecb67b7b' };
      scenario.actorFK = actorFK;

      const actorCollection: IActor[] = [{ actorID: '5e2b736a-7694-42cc-b866-7661aa86b5d0' }];
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
      const scenario: IScenario = { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const technologyFK: ITechnology = { technologyID: '523512eb-fd26-47ea-9b23-0a8246de7979' };
      scenario.technologyFK = technologyFK;

      const technologyCollection: ITechnology[] = [{ technologyID: '2f5f80f8-3355-4b98-baad-243b842a7228' }];
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
      const scenario: IScenario = { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const vulnerabilityFK: IVulnerability = { vulnerabilityID: '82f8bcb9-5a69-4228-b720-928fb578b47b' };
      scenario.vulnerabilityFK = vulnerabilityFK;

      const vulnerabilityCollection: IVulnerability[] = [{ vulnerabilityID: 'eee503fa-6942-4b13-b7ab-d5afe0aad546' }];
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
      const scenario: IScenario = { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const actorFK: IActor = { actorID: 'b0120454-2542-4415-a0c7-6acff86b0e97' };
      scenario.actorFK = actorFK;
      const technologyFK: ITechnology = { technologyID: '5a99f1f2-0629-4145-8065-e2ef410aeb1c' };
      scenario.technologyFK = technologyFK;
      const vulnerabilityFK: IVulnerability = { vulnerabilityID: 'a64e48c0-7686-425d-813e-53a0a289a37e' };
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
      const scenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
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
      const scenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
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
    describe('trackActorByActorID', () => {
      it('Should return tracked Actor primary key', () => {
        const entity = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const trackResult = comp.trackActorByActorID(0, entity);
        expect(trackResult).toEqual(entity.actorID);
      });
    });

    describe('trackTechnologyByTechnologyID', () => {
      it('Should return tracked Technology primary key', () => {
        const entity = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const trackResult = comp.trackTechnologyByTechnologyID(0, entity);
        expect(trackResult).toEqual(entity.technologyID);
      });
    });

    describe('trackVulnerabilityByVulnerabilityID', () => {
      it('Should return tracked Vulnerability primary key', () => {
        const entity = { vulnerabilityID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const trackResult = comp.trackVulnerabilityByVulnerabilityID(0, entity);
        expect(trackResult).toEqual(entity.vulnerabilityID);
      });
    });
  });
});
