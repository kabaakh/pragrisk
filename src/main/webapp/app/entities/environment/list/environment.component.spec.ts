import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EnvironmentService } from '../service/environment.service';

import { EnvironmentComponent } from './environment.component';

describe('Environment Management Component', () => {
  let comp: EnvironmentComponent;
  let fixture: ComponentFixture<EnvironmentComponent>;
  let service: EnvironmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'environment', component: EnvironmentComponent }]), HttpClientTestingModule],
      declarations: [EnvironmentComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(EnvironmentComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EnvironmentComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EnvironmentService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.environments?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
