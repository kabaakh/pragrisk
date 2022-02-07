import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EnvironmentComponent } from '../list/environment.component';
import { EnvironmentDetailComponent } from '../detail/environment-detail.component';
import { EnvironmentUpdateComponent } from '../update/environment-update.component';
import { EnvironmentRoutingResolveService } from './environment-routing-resolve.service';

const environmentRoute: Routes = [
  {
    path: '',
    component: EnvironmentComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EnvironmentDetailComponent,
    resolve: {
      environment: EnvironmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EnvironmentUpdateComponent,
    resolve: {
      environment: EnvironmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EnvironmentUpdateComponent,
    resolve: {
      environment: EnvironmentRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(environmentRoute)],
  exports: [RouterModule],
})
export class EnvironmentRoutingModule {}
