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
      const technology: ITechnology = { id: 456 };
      const parentTechnology: ITechnology = { id: 22986 };
      technology.parentTechnology = parentTechnology;

      const parentTechnologyCollection: ITechnology[] = [{ id: 22870 }];
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
      const technology: ITechnology = { id: 456 };
      const parentTechnology: ITechnology = { id: 45910 };
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
      const technology = { id: 123 };
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
      const technology = { id: 123 };
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
    describe('trackTechnologyById', () => {
      it('Should return tracked Technology primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTechnologyById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
