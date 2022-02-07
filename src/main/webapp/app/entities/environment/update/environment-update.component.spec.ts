import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EnvironmentService } from '../service/environment.service';
import { IEnvironment, Environment } from '../environment.model';

import { EnvironmentUpdateComponent } from './environment-update.component';

describe('Environment Management Update Component', () => {
  let comp: EnvironmentUpdateComponent;
  let fixture: ComponentFixture<EnvironmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let environmentService: EnvironmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EnvironmentUpdateComponent],
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
      .overrideTemplate(EnvironmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnvironmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    environmentService = TestBed.inject(EnvironmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const environment: IEnvironment = { id: 456 };

      activatedRoute.data = of({ environment });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(environment));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Environment>>();
      const environment = { id: 123 };
      jest.spyOn(environmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: environment }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(environmentService.update).toHaveBeenCalledWith(environment);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Environment>>();
      const environment = new Environment();
      jest.spyOn(environmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: environment }));
      saveSubject.complete();

      // THEN
      expect(environmentService.create).toHaveBeenCalledWith(environment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Environment>>();
      const environment = { id: 123 };
      jest.spyOn(environmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(environmentService.update).toHaveBeenCalledWith(environment);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
