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
      mitigationID: 'AAAAAAA',
      controlID: 'AAAAAAA',
      reference: 'AAAAAAA',
      type: MitigationType.PREVENTIVE,
      status: MitigationStatus.NOT_PERFORMED,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Mitigation', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
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
          mitigationID: 'BBBBBB',
          controlID: 'BBBBBB',
          reference: 'BBBBBB',
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
          reference: 'BBBBBB',
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
          mitigationID: 'BBBBBB',
          controlID: 'BBBBBB',
          reference: 'BBBBBB',
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
      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMitigationToCollectionIfMissing', () => {
      it('should add a Mitigation to an empty array', () => {
        const mitigation: IMitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addMitigationToCollectionIfMissing([], mitigation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mitigation);
      });

      it('should not add a Mitigation to an array that contains it', () => {
        const mitigation: IMitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const mitigationCollection: IMitigation[] = [
          {
            ...mitigation,
          },
          { mitigationID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
        ];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, mitigation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mitigation to an array that doesn't contain it", () => {
        const mitigation: IMitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const mitigationCollection: IMitigation[] = [{ mitigationID: '1361f429-3817-4123-8ee3-fdf8943310b2' }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, mitigation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mitigation);
      });

      it('should add only unique Mitigation to an array', () => {
        const mitigationArray: IMitigation[] = [
          { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' },
          { mitigationID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
          { mitigationID: 'd0a1720e-f5a9-4aa2-ba05-3b2183b1743b' },
        ];
        const mitigationCollection: IMitigation[] = [{ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, ...mitigationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mitigation: IMitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const mitigation2: IMitigation = { mitigationID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        expectedResult = service.addMitigationToCollectionIfMissing([], mitigation, mitigation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mitigation);
        expect(expectedResult).toContain(mitigation2);
      });

      it('should accept null and undefined values', () => {
        const mitigation: IMitigation = { mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addMitigationToCollectionIfMissing([], null, mitigation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mitigation);
      });

      it('should return initial array if no Mitigation is added', () => {
        const mitigationCollection: IMitigation[] = [{ mitigationID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addMitigationToCollectionIfMissing(mitigationCollection, undefined, null);
        expect(expectedResult).toEqual(mitigationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
