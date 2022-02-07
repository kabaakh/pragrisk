import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TechnologyComponent } from '../list/technology.component';
import { TechnologyDetailComponent } from '../detail/technology-detail.component';
import { TechnologyUpdateComponent } from '../update/technology-update.component';
import { TechnologyRoutingResolveService } from './technology-routing-resolve.service';

const technologyRoute: Routes = [
  {
    path: '',
    component: TechnologyComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':technologyID/view',
    component: TechnologyDetailComponent,
    resolve: {
      technology: TechnologyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TechnologyUpdateComponent,
    resolve: {
      technology: TechnologyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':technologyID/edit',
    component: TechnologyUpdateComponent,
    resolve: {
      technology: TechnologyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(technologyRoute)],
  exports: [RouterModule],
})
export class TechnologyRoutingModule {}
