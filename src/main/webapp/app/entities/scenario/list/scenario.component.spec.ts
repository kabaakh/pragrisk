import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ScenarioService } from '../service/scenario.service';

import { ScenarioComponent } from './scenario.component';

describe('Scenario Management Component', () => {
  let comp: ScenarioComponent;
  let fixture: ComponentFixture<ScenarioComponent>;
  let service: ScenarioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'scenario', component: ScenarioComponent }]), HttpClientTestingModule],
      declarations: [ScenarioComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(ScenarioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ScenarioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ScenarioService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' }],
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
    expect(comp.scenarios?.[0]).toEqual(expect.objectContaining({ scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });
});
