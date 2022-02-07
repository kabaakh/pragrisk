import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MitigationService } from '../service/mitigation.service';

import { MitigationComponent } from './mitigation.component';

describe('Mitigation Management Component', () => {
  let comp: MitigationComponent;
  let fixture: ComponentFixture<MitigationComponent>;
  let service: MitigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'mitigation', component: MitigationComponent }]), HttpClientTestingModule],
      declarations: [MitigationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(MitigationComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MitigationComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MitigationService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' }],
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
    expect(comp.mitigations?.[0]).toEqual(expect.objectContaining({ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });
});
