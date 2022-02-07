import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TechCategory } from 'app/entities/enumerations/tech-category.model';
import { TechStack } from 'app/entities/enumerations/tech-stack.model';
import { ITechnology, Technology } from '../technology.model';

import { TechnologyService } from './technology.service';

describe('Technology Service', () => {
  let service: TechnologyService;
  let httpMock: HttpTestingController;
  let elemDefault: ITechnology;
  let expectedResult: ITechnology | ITechnology[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TechnologyService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      category: TechCategory.APPLICATION,
      description: 'AAAAAAA',
      techStackType: TechStack.JAVA,
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

    it('should create a Technology', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Technology()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Technology', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          category: 'BBBBBB',
          description: 'BBBBBB',
          techStackType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Technology', () => {
      const patchObject = Object.assign(
        {
          category: 'BBBBBB',
        },
        new Technology()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Technology', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          category: 'BBBBBB',
          description: 'BBBBBB',
          techStackType: 'BBBBBB',
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

    it('should delete a Technology', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTechnologyToCollectionIfMissing', () => {
      it('should add a Technology to an empty array', () => {
        const technology: ITechnology = { id: 123 };
        expectedResult = service.addTechnologyToCollectionIfMissing([], technology);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technology);
      });

      it('should not add a Technology to an array that contains it', () => {
        const technology: ITechnology = { id: 123 };
        const technologyCollection: ITechnology[] = [
          {
            ...technology,
          },
          { id: 456 },
        ];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, technology);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Technology to an array that doesn't contain it", () => {
        const technology: ITechnology = { id: 123 };
        const technologyCollection: ITechnology[] = [{ id: 456 }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, technology);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technology);
      });

      it('should add only unique Technology to an array', () => {
        const technologyArray: ITechnology[] = [{ id: 123 }, { id: 456 }, { id: 88670 }];
        const technologyCollection: ITechnology[] = [{ id: 123 }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, ...technologyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const technology: ITechnology = { id: 123 };
        const technology2: ITechnology = { id: 456 };
        expectedResult = service.addTechnologyToCollectionIfMissing([], technology, technology2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technology);
        expect(expectedResult).toContain(technology2);
      });

      it('should accept null and undefined values', () => {
        const technology: ITechnology = { id: 123 };
        expectedResult = service.addTechnologyToCollectionIfMissing([], null, technology, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technology);
      });

      it('should return initial array if no Technology is added', () => {
        const technologyCollection: ITechnology[] = [{ id: 123 }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, undefined, null);
        expect(expectedResult).toEqual(technologyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
