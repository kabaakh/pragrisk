import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EnvironmentComponent } from './list/environment.component';
import { EnvironmentDetailComponent } from './detail/environment-detail.component';
import { EnvironmentUpdateComponent } from './update/environment-update.component';
import { EnvironmentDeleteDialogComponent } from './delete/environment-delete-dialog.component';
import { EnvironmentRoutingModule } from './route/environment-routing.module';

@NgModule({
  imports: [SharedModule, EnvironmentRoutingModule],
  declarations: [EnvironmentComponent, EnvironmentDetailComponent, EnvironmentUpdateComponent, EnvironmentDeleteDialogComponent],
  entryComponents: [EnvironmentDeleteDialogComponent],
})
export class EnvironmentModule {}
