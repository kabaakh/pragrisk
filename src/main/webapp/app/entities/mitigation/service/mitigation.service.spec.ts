import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { MitigationType } from 'app/entities/enumerations/mitigation-type.model';
import { MitigationStatus } from 'app/entities/enumerations/mitigation-status.model';
import { IMitigation, Mitigation } from '../mitigation.model';

import { MitigationService } from './mitigation.service';

describe('Mitigation Service', () => {
  let service: MitigationService;
  let httpMock: HttpTestingController;
  let elemDefault: IMitigation;
  let expectedResult: IMitigation | IMitigation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MitigationService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      controlID: 'AAAAAAA',
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      frameworkReference: 'AAAAAAA',
      type: MitigationType.PREVENTIVE,
      status: MitigationStatus.NOT_PERFORMED,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Mitigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Mitigation()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mitigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          controlID: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          frameworkReference: 'BBBBBB',
          type: 'BBBBBB',
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Mitigation', () => {
      const patchObject = Object.assign(
        {
          controlID: 'BBBBBB',
          title: 'BBBBBB',
          type: 'BBBBBB',
        },
        new Mitigation()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mitigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          controlID: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          frameworkReference: 'BBBBBB',
          type: 'BBBBBB',
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Mitigation', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMitigationToCollectionIfMissing', () => {
      it('should add a Mitigation to an empty array', () => {
        const mitigation: IMitigation = { id: 123 };
        expectedResult = service.addMitigationToCollectionIfMissing([], mitigation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mitigation);
      });

      it('should not add a Mitigation to an array that contains it', () => {
        const mitigation: IMitigation = { id: 123 };
        const mitigationCollection: IMitigation[] = [
          {
            ...mitigation,
          },
          { id: 456 },
        ];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, mitigation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mitigation to an array that doesn't contain it", () => {
        const mitigation: IMitigation = { id: 123 };
        const mitigationCollection: IMitigation[] = [{ id: 456 }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, mitigation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mitigation);
      });

      it('should add only unique Mitigation to an array', () => {
        const mitigationArray: IMitigation[] = [{ id: 123 }, { id: 456 }, { id: 65154 }];
        const mitigationCollection: IMitigation[] = [{ id: 123 }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, ...mitigationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mitigation: IMitigation = { id: 123 };
        const mitigation2: IMitigation = { id: 456 };
        expectedResult = service.addMitigationToCollectionIfMissing([], mitigation, mitigation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mitigation);
        expect(expectedResult).toContain(mitigation2);
      });

      it('should accept null and undefined values', () => {
        const mitigation: IMitigation = { id: 123 };
        expectedResult = service.addMitigationToCollectionIfMissing([], null, mitigation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mitigation);
      });

      it('should return initial array if no Mitigation is added', () => {
        const mitigationCollection: IMitigation[] = [{ id: 123 }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, undefined, null);
        expect(expectedResult).toEqual(mitigationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
