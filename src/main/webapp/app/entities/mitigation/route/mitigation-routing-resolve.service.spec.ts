import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IMitigation, Mitigation } from '../mitigation.model';
import { MitigationService } from '../service/mitigation.service';

import { MitigationRoutingResolveService } from './mitigation-routing-resolve.service';

describe('Mitigation routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: MitigationRoutingResolveService;
  let service: MitigationService;
  let resultMitigation: IMitigation | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(MitigationRoutingResolveService);
    service = TestBed.inject(MitigationService);
    resultMitigation = undefined;
  });

  describe('resolve', () => {
    it('should return IMitigation returned by find', () => {
      // GIVEN
      service.find = jest.fn(mitigationID => of(new HttpResponse({ body: { mitigationID } })));
      mockActivatedRouteSnapshot.params = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMitigation = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('9fec3727-3421-4967-b213-ba36557ca194');
      expect(resultMitigation).toEqual({ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' });
    });

    it('should return new IMitigation if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMitigation = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultMitigation).toEqual(new Mitigation());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Mitigation })));
      mockActivatedRouteSnapshot.params = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultMitigation = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('9fec3727-3421-4967-b213-ba36557ca194');
      expect(resultMitigation).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
