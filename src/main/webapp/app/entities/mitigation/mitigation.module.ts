import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MitigationComponent } from './list/mitigation.component';
import { MitigationDetailComponent } from './detail/mitigation-detail.component';
import { MitigationUpdateComponent } from './update/mitigation-update.component';
import { MitigationDeleteDialogComponent } from './delete/mitigation-delete-dialog.component';
import { MitigationRoutingModule } from './route/mitigation-routing.module';

@NgModule({
  imports: [SharedModule, MitigationRoutingModule],
  declarations: [MitigationComponent, MitigationDetailComponent, MitigationUpdateComponent, MitigationDeleteDialogComponent],
  entryComponents: [MitigationDeleteDialogComponent],
})
export class MitigationModule {}
