import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Environment } from 'app/entities/enumerations/environment.model';
import { IActor, Actor } from '../actor.model';

import { ActorService } from './actor.service';

describe('Actor Service', () => {
  let service: ActorService;
  let httpMock: HttpTestingController;
  let elemDefault: IActor;
  let expectedResult: IActor | IActor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ActorService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      actorID: 'AAAAAAA',
      firstName: 'AAAAAAA',
      lastName: 'AAAAAAA',
      nickName: 'AAAAAAA',
      group: Environment.KOM,
      description: 'AAAAAAA',
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

    it('should create a Actor', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Actor()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Actor', () => {
      const returnedFromService = Object.assign(
        {
          actorID: 'BBBBBB',
          firstName: 'BBBBBB',
          lastName: 'BBBBBB',
          nickName: 'BBBBBB',
          group: 'BBBBBB',
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

    it('should partial update a Actor', () => {
      const patchObject = Object.assign(
        {
          firstName: 'BBBBBB',
          lastName: 'BBBBBB',
        },
        new Actor()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Actor', () => {
      const returnedFromService = Object.assign(
        {
          actorID: 'BBBBBB',
          firstName: 'BBBBBB',
          lastName: 'BBBBBB',
          nickName: 'BBBBBB',
          group: 'BBBBBB',
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

    it('should delete a Actor', () => {
      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addActorToCollectionIfMissing', () => {
      it('should add a Actor to an empty array', () => {
        const actor: IActor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addActorToCollectionIfMissing([], actor);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(actor);
      });

      it('should not add a Actor to an array that contains it', () => {
        const actor: IActor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const actorCollection: IActor[] = [
          {
            ...actor,
          },
          { actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
        ];
        expectedResult = service.addActorToCollectionIfMissing(actorCollection, actor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Actor to an array that doesn't contain it", () => {
        const actor: IActor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const actorCollection: IActor[] = [{ actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' }];
        expectedResult = service.addActorToCollectionIfMissing(actorCollection, actor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(actor);
      });

      it('should add only unique Actor to an array', () => {
        const actorArray: IActor[] = [
          { actorID: '9fec3727-3421-4967-b213-ba36557ca194' },
          { actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
          { actorID: 'ad7f2eed-b0ee-4dd6-8579-99f7b5116334' },
        ];
        const actorCollection: IActor[] = [{ actorID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addActorToCollectionIfMissing(actorCollection, ...actorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const actor: IActor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const actor2: IActor = { actorID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        expectedResult = service.addActorToCollectionIfMissing([], actor, actor2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(actor);
        expect(expectedResult).toContain(actor2);
      });

      it('should accept null and undefined values', () => {
        const actor: IActor = { actorID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addActorToCollectionIfMissing([], null, actor, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(actor);
      });

      it('should return initial array if no Actor is added', () => {
        const actorCollection: IActor[] = [{ actorID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addActorToCollectionIfMissing(actorCollection, undefined, null);
        expect(expectedResult).toEqual(actorCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
