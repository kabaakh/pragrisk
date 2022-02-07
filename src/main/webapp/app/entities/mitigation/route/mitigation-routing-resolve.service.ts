import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMitigation, Mitigation } from '../mitigation.model';
import { MitigationService } from '../service/mitigation.service';

@Injectable({ providedIn: 'root' })
export class MitigationRoutingResolveService implements Resolve<IMitigation> {
  constructor(protected service: MitigationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMitigation> | Observable<never> {
    const id = route.params['vulnerabiltyID'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((mitigation: HttpResponse<Mitigation>) => {
          if (mitigation.body) {
            return of(mitigation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Mitigation());
  }
}
