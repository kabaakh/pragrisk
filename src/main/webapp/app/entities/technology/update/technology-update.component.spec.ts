import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TechnologyService } from '../service/technology.service';
import { ITechnology, Technology } from '../technology.model';

import { TechnologyUpdateComponent } from './technology-update.component';

describe('Technology Management Update Component', () => {
  let comp: TechnologyUpdateComponent;
  let fixture: ComponentFixture<TechnologyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let technologyService: TechnologyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TechnologyUpdateComponent],
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
      .overrideTemplate(TechnologyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TechnologyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    technologyService = TestBed.inject(TechnologyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call parentTechnology query and add missing value', () => {
      const technology: ITechnology = { technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const parentTechnology: ITechnology = { technologyID: '33746470-9f53-4489-ada3-c78bba18f050' };
      technology.parentTechnology = parentTechnology;

      const parentTechnologyCollection: ITechnology[] = [{ technologyID: 'a98630a8-0947-4df4-a91b-8244d498a6ea' }];
      jest.spyOn(technologyService, 'query').mockReturnValue(of(new HttpResponse({ body: parentTechnologyCollection })));
      const expectedCollection: ITechnology[] = [parentTechnology, ...parentTechnologyCollection];
      jest.spyOn(technologyService, 'addTechnologyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ technology });
      comp.ngOnInit();

      expect(technologyService.query).toHaveBeenCalled();
      expect(technologyService.addTechnologyToCollectionIfMissing).toHaveBeenCalledWith(parentTechnologyCollection, parentTechnology);
      expect(comp.parentTechnologiesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const technology: ITechnology = { technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
      const parentTechnology: ITechnology = { technologyID: 'f8f80981-46e9-4ae6-96a3-13f38e0703d5' };
      technology.parentTechnology = parentTechnology;

      activatedRoute.data = of({ technology });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(technology));
      expect(comp.parentTechnologiesCollection).toContain(parentTechnology);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Technology>>();
      const technology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(technologyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technology });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: technology }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(technologyService.update).toHaveBeenCalledWith(technology);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Technology>>();
      const technology = new Technology();
      jest.spyOn(technologyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technology });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: technology }));
      saveSubject.complete();

      // THEN
      expect(technologyService.create).toHaveBeenCalledWith(technology);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Technology>>();
      const technology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(technologyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ technology });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(technologyService.update).toHaveBeenCalledWith(technology);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTechnologyByTechnologyID', () => {
      it('Should return tracked Technology primary key', () => {
        const entity = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const trackResult = comp.trackTechnologyByTechnologyID(0, entity);
        expect(trackResult).toEqual(entity.technologyID);
      });
    });
  });
});
