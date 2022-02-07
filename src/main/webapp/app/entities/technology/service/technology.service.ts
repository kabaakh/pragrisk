import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITechnology, getTechnologyIdentifier } from '../technology.model';

export type EntityResponseType = HttpResponse<ITechnology>;
export type EntityArrayResponseType = HttpResponse<ITechnology[]>;

@Injectable({ providedIn: 'root' })
export class TechnologyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/technologies');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/technologies');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(technology: ITechnology): Observable<EntityResponseType> {
    return this.http.post<ITechnology>(this.resourceUrl, technology, { observe: 'response' });
  }

  update(technology: ITechnology): Observable<EntityResponseType> {
    return this.http.put<ITechnology>(`${this.resourceUrl}/${getTechnologyIdentifier(technology) as number}`, technology, {
      observe: 'response',
    });
  }

  partialUpdate(technology: ITechnology): Observable<EntityResponseType> {
    return this.http.patch<ITechnology>(`${this.resourceUrl}/${getTechnologyIdentifier(technology) as number}`, technology, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITechnology>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITechnology[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITechnology[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addTechnologyToCollectionIfMissing(
    technologyCollection: ITechnology[],
    ...technologiesToCheck: (ITechnology | null | undefined)[]
  ): ITechnology[] {
    const technologies: ITechnology[] = technologiesToCheck.filter(isPresent);
    if (technologies.length > 0) {
      const technologyCollectionIdentifiers = technologyCollection.map(technologyItem => getTechnologyIdentifier(technologyItem)!);
      const technologiesToAdd = technologies.filter(technologyItem => {
        const technologyIdentifier = getTechnologyIdentifier(technologyItem);
        if (technologyIdentifier == null || technologyCollectionIdentifiers.includes(technologyIdentifier)) {
          return false;
        }
        technologyCollectionIdentifiers.push(technologyIdentifier);
        return true;
      });
      return [...technologiesToAdd, ...technologyCollection];
    }
    return technologyCollection;
  }
}
