import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IMitigation, getMitigationIdentifier } from '../mitigation.model';

export type EntityResponseType = HttpResponse<IMitigation>;
export type EntityArrayResponseType = HttpResponse<IMitigation[]>;

@Injectable({ providedIn: 'root' })
export class MitigationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mitigations');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/mitigations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(mitigation: IMitigation): Observable<EntityResponseType> {
    return this.http.post<IMitigation>(this.resourceUrl, mitigation, { observe: 'response' });
  }

  update(mitigation: IMitigation): Observable<EntityResponseType> {
    return this.http.put<IMitigation>(`${this.resourceUrl}/${getMitigationIdentifier(mitigation) as number}`, mitigation, {
      observe: 'response',
    });
  }

  partialUpdate(mitigation: IMitigation): Observable<EntityResponseType> {
    return this.http.patch<IMitigation>(`${this.resourceUrl}/${getMitigationIdentifier(mitigation) as number}`, mitigation, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMitigation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMitigation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMitigation[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addMitigationToCollectionIfMissing(
    mitigationCollection: IMitigation[],
    ...mitigationsToCheck: (IMitigation | null | undefined)[]
  ): IMitigation[] {
    const mitigations: IMitigation[] = mitigationsToCheck.filter(isPresent);
    if (mitigations.length > 0) {
      const mitigationCollectionIdentifiers = mitigationCollection.map(mitigationItem => getMitigationIdentifier(mitigationItem)!);
      const mitigationsToAdd = mitigations.filter(mitigationItem => {
        const mitigationIdentifier = getMitigationIdentifier(mitigationItem);
        if (mitigationIdentifier == null || mitigationCollectionIdentifiers.includes(mitigationIdentifier)) {
          return false;
        }
        mitigationCollectionIdentifiers.push(mitigationIdentifier);
        return true;
      });
      return [...mitigationsToAdd, ...mitigationCollection];
    }
    return mitigationCollection;
  }
}
