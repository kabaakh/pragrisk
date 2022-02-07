import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IActor, getActorIdentifier } from '../actor.model';

export type EntityResponseType = HttpResponse<IActor>;
export type EntityArrayResponseType = HttpResponse<IActor[]>;

@Injectable({ providedIn: 'root' })
export class ActorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/actors');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/actors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(actor: IActor): Observable<EntityResponseType> {
    return this.http.post<IActor>(this.resourceUrl, actor, { observe: 'response' });
  }

  update(actor: IActor): Observable<EntityResponseType> {
    return this.http.put<IActor>(`${this.resourceUrl}/${getActorIdentifier(actor) as number}`, actor, { observe: 'response' });
  }

  partialUpdate(actor: IActor): Observable<EntityResponseType> {
    return this.http.patch<IActor>(`${this.resourceUrl}/${getActorIdentifier(actor) as number}`, actor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IActor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IActor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IActor[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addActorToCollectionIfMissing(actorCollection: IActor[], ...actorsToCheck: (IActor | null | undefined)[]): IActor[] {
    const actors: IActor[] = actorsToCheck.filter(isPresent);
    if (actors.length > 0) {
      const actorCollectionIdentifiers = actorCollection.map(actorItem => getActorIdentifier(actorItem)!);
      const actorsToAdd = actors.filter(actorItem => {
        const actorIdentifier = getActorIdentifier(actorItem);
        if (actorIdentifier == null || actorCollectionIdentifiers.includes(actorIdentifier)) {
          return false;
        }
        actorCollectionIdentifiers.push(actorIdentifier);
        return true;
      });
      return [...actorsToAdd, ...actorCollection];
    }
    return actorCollection;
  }
}
