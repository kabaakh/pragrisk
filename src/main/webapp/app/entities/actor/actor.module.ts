import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ActorComponent } from './list/actor.component';
import { ActorDetailComponent } from './detail/actor-detail.component';
import { ActorUpdateComponent } from './update/actor-update.component';
import { ActorDeleteDialogComponent } from './delete/actor-delete-dialog.component';
import { ActorRoutingModule } from './route/actor-routing.module';

@NgModule({
  imports: [SharedModule, ActorRoutingModule],
  declarations: [ActorComponent, ActorDetailComponent, ActorUpdateComponent, ActorDeleteDialogComponent],
  entryComponents: [ActorDeleteDialogComponent],
})
export class ActorModule {}
