import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEnvironment, Environment } from '../environment.model';
import { EnvironmentService } from '../service/environment.service';

@Injectable({ providedIn: 'root' })
export class EnvironmentRoutingResolveService implements Resolve<IEnvironment> {
  constructor(protected service: EnvironmentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEnvironment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((environment: HttpResponse<Environment>) => {
          if (environment.body) {
            return of(environment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Environment());
  }
}
