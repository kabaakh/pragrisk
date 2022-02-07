import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TechnologyService } from '../service/technology.service';

import { TechnologyComponent } from './technology.component';

describe('Technology Management Component', () => {
  let comp: TechnologyComponent;
  let fixture: ComponentFixture<TechnologyComponent>;
  let service: TechnologyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'technology', component: TechnologyComponent }]), HttpClientTestingModule],
      declarations: [TechnologyComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(TechnologyComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TechnologyComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TechnologyService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ technologyID: '9fec3727-3421-4967-b213-ba36557ca194' }],
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
    expect(comp.technologies?.[0]).toEqual(expect.objectContaining({ technologyID: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });
});
