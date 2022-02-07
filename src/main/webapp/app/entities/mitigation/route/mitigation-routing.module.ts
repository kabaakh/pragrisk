import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MitigationComponent } from '../list/mitigation.component';
import { MitigationDetailComponent } from '../detail/mitigation-detail.component';
import { MitigationUpdateComponent } from '../update/mitigation-update.component';
import { MitigationRoutingResolveService } from './mitigation-routing-resolve.service';

const mitigationRoute: Routes = [
  {
    path: '',
    component: MitigationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MitigationDetailComponent,
    resolve: {
      mitigation: MitigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MitigationUpdateComponent,
    resolve: {
      mitigation: MitigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MitigationUpdateComponent,
    resolve: {
      mitigation: MitigationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(mitigationRoute)],
  exports: [RouterModule],
})
export class MitigationRoutingModule {}
