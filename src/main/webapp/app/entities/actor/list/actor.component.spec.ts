import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ActorService } from '../service/actor.service';

import { ActorComponent } from './actor.component';

describe('Actor Management Component', () => {
  let comp: ActorComponent;
  let fixture: ComponentFixture<ActorComponent>;
  let service: ActorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'actor', component: ActorComponent }]), HttpClientTestingModule],
      declarations: [ActorComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(ActorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ActorService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ actorID: '9fec3727-3421-4967-b213-ba36557ca194' }],
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
    expect(comp.actors?.[0]).toEqual(expect.objectContaining({ actorID: '9fec3727-3421-4967-b213-ba36557ca194' }));
  });
});
