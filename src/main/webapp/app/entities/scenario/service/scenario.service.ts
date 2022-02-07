import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IScenario, getScenarioIdentifier } from '../scenario.model';

export type EntityResponseType = HttpResponse<IScenario>;
export type EntityArrayResponseType = HttpResponse<IScenario[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/scenarios');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/scenarios');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(scenario: IScenario): Observable<EntityResponseType> {
    return this.http.post<IScenario>(this.resourceUrl, scenario, { observe: 'response' });
  }

  update(scenario: IScenario): Observable<EntityResponseType> {
    return this.http.put<IScenario>(`${this.resourceUrl}/${getScenarioIdentifier(scenario) as string}`, scenario, { observe: 'response' });
  }

  partialUpdate(scenario: IScenario): Observable<EntityResponseType> {
    return this.http.patch<IScenario>(`${this.resourceUrl}/${getScenarioIdentifier(scenario) as string}`, scenario, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IScenario>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IScenario[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IScenario[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addScenarioToCollectionIfMissing(scenarioCollection: IScenario[], ...scenariosToCheck: (IScenario | null | undefined)[]): IScenario[] {
    const scenarios: IScenario[] = scenariosToCheck.filter(isPresent);
    if (scenarios.length > 0) {
      const scenarioCollectionIdentifiers = scenarioCollection.map(scenarioItem => getScenarioIdentifier(scenarioItem)!);
      const scenariosToAdd = scenarios.filter(scenarioItem => {
        const scenarioIdentifier = getScenarioIdentifier(scenarioItem);
        if (scenarioIdentifier == null || scenarioCollectionIdentifiers.includes(scenarioIdentifier)) {
          return false;
        }
        scenarioCollectionIdentifiers.push(scenarioIdentifier);
        return true;
      });
      return [...scenariosToAdd, ...scenarioCollection];
    }
    return scenarioCollection;
  }
}
