import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IEnvironment, getEnvironmentIdentifier } from '../environment.model';

export type EntityResponseType = HttpResponse<IEnvironment>;
export type EntityArrayResponseType = HttpResponse<IEnvironment[]>;

@Injectable({ providedIn: 'root' })
export class EnvironmentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/environments');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/environments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(environment: IEnvironment): Observable<EntityResponseType> {
    return this.http.post<IEnvironment>(this.resourceUrl, environment, { observe: 'response' });
  }

  update(environment: IEnvironment): Observable<EntityResponseType> {
    return this.http.put<IEnvironment>(`${this.resourceUrl}/${getEnvironmentIdentifier(environment) as number}`, environment, {
      observe: 'response',
    });
  }

  partialUpdate(environment: IEnvironment): Observable<EntityResponseType> {
    return this.http.patch<IEnvironment>(`${this.resourceUrl}/${getEnvironmentIdentifier(environment) as number}`, environment, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEnvironment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnvironment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEnvironment[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addEnvironmentToCollectionIfMissing(
    environmentCollection: IEnvironment[],
    ...environmentsToCheck: (IEnvironment | null | undefined)[]
  ): IEnvironment[] {
    const environments: IEnvironment[] = environmentsToCheck.filter(isPresent);
    if (environments.length > 0) {
      const environmentCollectionIdentifiers = environmentCollection.map(environmentItem => getEnvironmentIdentifier(environmentItem)!);
      const environmentsToAdd = environments.filter(environmentItem => {
        const environmentIdentifier = getEnvironmentIdentifier(environmentItem);
        if (environmentIdentifier == null || environmentCollectionIdentifiers.includes(environmentIdentifier)) {
          return false;
        }
        environmentCollectionIdentifiers.push(environmentIdentifier);
        return true;
      });
      return [...environmentsToAdd, ...environmentCollection];
    }
    return environmentCollection;
  }
}
