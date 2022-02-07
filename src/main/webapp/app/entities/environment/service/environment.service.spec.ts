import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEnvironment, Environment } from '../environment.model';

import { EnvironmentService } from './environment.service';

describe('Environment Service', () => {
  let service: EnvironmentService;
  let httpMock: HttpTestingController;
  let elemDefault: IEnvironment;
  let expectedResult: IEnvironment | IEnvironment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EnvironmentService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
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

    it('should create a Environment', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Environment()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Environment', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Environment', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Environment()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Environment', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
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

    it('should delete a Environment', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEnvironmentToCollectionIfMissing', () => {
      it('should add a Environment to an empty array', () => {
        const environment: IEnvironment = { id: 123 };
        expectedResult = service.addEnvironmentToCollectionIfMissing([], environment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(environment);
      });

      it('should not add a Environment to an array that contains it', () => {
        const environment: IEnvironment = { id: 123 };
        const environmentCollection: IEnvironment[] = [
          {
            ...environment,
          },
          { id: 456 },
        ];
        expectedResult = service.addEnvironmentToCollectionIfMissing(environmentCollection, environment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Environment to an array that doesn't contain it", () => {
        const environment: IEnvironment = { id: 123 };
        const environmentCollection: IEnvironment[] = [{ id: 456 }];
        expectedResult = service.addEnvironmentToCollectionIfMissing(environmentCollection, environment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(environment);
      });

      it('should add only unique Environment to an array', () => {
        const environmentArray: IEnvironment[] = [{ id: 123 }, { id: 456 }, { id: 80187 }];
        const environmentCollection: IEnvironment[] = [{ id: 123 }];
        expectedResult = service.addEnvironmentToCollectionIfMissing(environmentCollection, ...environmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const environment: IEnvironment = { id: 123 };
        const environment2: IEnvironment = { id: 456 };
        expectedResult = service.addEnvironmentToCollectionIfMissing([], environment, environment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(environment);
        expect(expectedResult).toContain(environment2);
      });

      it('should accept null and undefined values', () => {
        const environment: IEnvironment = { id: 123 };
        expectedResult = service.addEnvironmentToCollectionIfMissing([], null, environment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(environment);
      });

      it('should return initial array if no Environment is added', () => {
        const environmentCollection: IEnvironment[] = [{ id: 123 }];
        expectedResult = service.addEnvironmentToCollectionIfMissing(environmentCollection, undefined, null);
        expect(expectedResult).toEqual(environmentCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
