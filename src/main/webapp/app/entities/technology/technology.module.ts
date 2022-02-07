import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TechnologyComponent } from './list/technology.component';
import { TechnologyDetailComponent } from './detail/technology-detail.component';
import { TechnologyUpdateComponent } from './update/technology-update.component';
import { TechnologyDeleteDialogComponent } from './delete/technology-delete-dialog.component';
import { TechnologyRoutingModule } from './route/technology-routing.module';

@NgModule({
  imports: [SharedModule, TechnologyRoutingModule],
  declarations: [TechnologyComponent, TechnologyDetailComponent, TechnologyUpdateComponent, TechnologyDeleteDialogComponent],
  entryComponents: [TechnologyDeleteDialogComponent],
})
export class TechnologyModule {}
