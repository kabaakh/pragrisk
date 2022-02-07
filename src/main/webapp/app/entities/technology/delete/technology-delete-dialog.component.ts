import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITechnology } from '../technology.model';
import { TechnologyService } from '../service/technology.service';

@Component({
  templateUrl: './technology-delete-dialog.component.html',
})
export class TechnologyDeleteDialogComponent {
  technology?: ITechnology;

  constructor(protected technologyService: TechnologyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.technologyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
