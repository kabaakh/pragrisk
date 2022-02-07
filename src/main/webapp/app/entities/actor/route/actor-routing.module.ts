import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ActorComponent } from '../list/actor.component';
import { ActorDetailComponent } from '../detail/actor-detail.component';
import { ActorUpdateComponent } from '../update/actor-update.component';
import { ActorRoutingResolveService } from './actor-routing-resolve.service';

const actorRoute: Routes = [
  {
    path: '',
    component: ActorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':actorID/view',
    component: ActorDetailComponent,
    resolve: {
      actor: ActorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ActorUpdateComponent,
    resolve: {
      actor: ActorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':actorID/edit',
    component: ActorUpdateComponent,
    resolve: {
      actor: ActorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(actorRoute)],
  exports: [RouterModule],
})
export class ActorRoutingModule {}
