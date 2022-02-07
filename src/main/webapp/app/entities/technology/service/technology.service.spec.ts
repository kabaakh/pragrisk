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
      technologyID: 'AAAAAAA',
      name: 'AAAAAAA',
      category: TechCategory.FAG,
      description: 'AAAAAAA',
      techStackType: TechStack.JAVA,
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

    it('should create a Technology', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
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
          technologyID: 'BBBBBB',
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
          technologyID: 'BBBBBB',
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
      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTechnologyToCollectionIfMissing', () => {
      it('should add a Technology to an empty array', () => {
        const technology: ITechnology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addTechnologyToCollectionIfMissing([], technology);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technology);
      });

      it('should not add a Technology to an array that contains it', () => {
        const technology: ITechnology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const technologyCollection: ITechnology[] = [
          {
            ...technology,
          },
          { technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
        ];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, technology);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Technology to an array that doesn't contain it", () => {
        const technology: ITechnology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const technologyCollection: ITechnology[] = [{ technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, technology);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technology);
      });

      it('should add only unique Technology to an array', () => {
        const technologyArray: ITechnology[] = [
          { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' },
          { technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
          { technologyID: 'e4858b99-4988-4ffe-9880-29b0a96bc550' },
        ];
        const technologyCollection: ITechnology[] = [{ technologyID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, ...technologyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const technology: ITechnology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const technology2: ITechnology = { technologyID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        expectedResult = service.addTechnologyToCollectionIfMissing([], technology, technology2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(technology);
        expect(expectedResult).toContain(technology2);
      });

      it('should accept null and undefined values', () => {
        const technology: ITechnology = { technologyID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addTechnologyToCollectionIfMissing([], null, technology, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(technology);
      });

      it('should return initial array if no Technology is added', () => {
        const technologyCollection: ITechnology[] = [{ technologyID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addTechnologyToCollectionIfMissing(technologyCollection, undefined, null);
        expect(expectedResult).toEqual(technologyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
