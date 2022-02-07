import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MitigationService } from '../service/mitigation.service';
import { IMitigation, Mitigation } from '../mitigation.model';

import { MitigationUpdateComponent } from './mitigation-update.component';

describe('Mitigation Management Update Component', () => {
  let comp: MitigationUpdateComponent;
  let fixture: ComponentFixture<MitigationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let mitigationService: MitigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MitigationUpdateComponent],
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
      .overrideTemplate(MitigationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MitigationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mitigationService = TestBed.inject(MitigationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const mitigation: IMitigation = { mitigationID: '1361f429-3817-4123-8ee3-fdf8943310b2' };

      activatedRoute.data = of({ mitigation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(mitigation));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mitigation>>();
      const mitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(mitigationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mitigation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mitigation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(mitigationService.update).toHaveBeenCalledWith(mitigation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mitigation>>();
      const mitigation = new Mitigation();
      jest.spyOn(mitigationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mitigation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mitigation }));
      saveSubject.complete();

      // THEN
      expect(mitigationService.create).toHaveBeenCalledWith(mitigation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mitigation>>();
      const mitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(mitigationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mitigation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mitigationService.update).toHaveBeenCalledWith(mitigation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
