import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IScenario, Scenario } from '../scenario.model';

import { ScenarioService } from './scenario.service';

describe('Scenario Service', () => {
  let service: ScenarioService;
  let httpMock: HttpTestingController;
  let elemDefault: IScenario;
  let expectedResult: IScenario | IScenario[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ScenarioService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      scenarioID: 'AAAAAAA',
      actorFK: 'AAAAAAA',
      technologyFK: 'AAAAAAA',
      vulnerabilityFK: 'AAAAAAA',
      description: 'AAAAAAA',
      probability: 0,
      qonsequence: 0,
      riskValue: 0,
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

    it('should create a Scenario', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Scenario()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Scenario', () => {
      const returnedFromService = Object.assign(
        {
          scenarioID: 'BBBBBB',
          actorFK: 'BBBBBB',
          technologyFK: 'BBBBBB',
          vulnerabilityFK: 'BBBBBB',
          description: 'BBBBBB',
          probability: 1,
          qonsequence: 1,
          riskValue: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Scenario', () => {
      const patchObject = Object.assign(
        {
          technologyFK: 'BBBBBB',
          vulnerabilityFK: 'BBBBBB',
          probability: 1,
          qonsequence: 1,
        },
        new Scenario()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Scenario', () => {
      const returnedFromService = Object.assign(
        {
          scenarioID: 'BBBBBB',
          actorFK: 'BBBBBB',
          technologyFK: 'BBBBBB',
          vulnerabilityFK: 'BBBBBB',
          description: 'BBBBBB',
          probability: 1,
          qonsequence: 1,
          riskValue: 1,
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

    it('should delete a Scenario', () => {
      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addScenarioToCollectionIfMissing', () => {
      it('should add a Scenario to an empty array', () => {
        const scenario: IScenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should not add a Scenario to an array that contains it', () => {
        const scenario: IScenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const scenarioCollection: IScenario[] = [
          {
            ...scenario,
          },
          { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
        ];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Scenario to an array that doesn't contain it", () => {
        const scenario: IScenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const scenarioCollection: IScenario[] = [{ scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, scenario);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
      });

      it('should add only unique Scenario to an array', () => {
        const scenarioArray: IScenario[] = [
          { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' },
          { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' },
          { scenarioID: '30287ba7-e7e9-4bbd-af29-8fcaccb3c164' },
        ];
        const scenarioCollection: IScenario[] = [{ scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, ...scenarioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const scenario: IScenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
        const scenario2: IScenario = { scenarioID: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        expectedResult = service.addScenarioToCollectionIfMissing([], scenario, scenario2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(scenario);
        expect(expectedResult).toContain(scenario2);
      });

      it('should accept null and undefined values', () => {
        const scenario: IScenario = { scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addScenarioToCollectionIfMissing([], null, scenario, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(scenario);
      });

      it('should return initial array if no Scenario is added', () => {
        const scenarioCollection: IScenario[] = [{ scenarioID: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addScenarioToCollectionIfMissing(scenarioCollection, undefined, null);
        expect(expectedResult).toEqual(scenarioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
