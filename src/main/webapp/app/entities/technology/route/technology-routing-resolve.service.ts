import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITechnology, Technology } from '../technology.model';
import { TechnologyService } from '../service/technology.service';

@Injectable({ providedIn: 'root' })
export class TechnologyRoutingResolveService implements Resolve<ITechnology> {
  constructor(protected service: TechnologyService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITechnology> | Observable<never> {
    const id = route.params['technologyID'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((technology: HttpResponse<Technology>) => {
          if (technology.body) {
            return of(technology.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Technology());
  }
}
