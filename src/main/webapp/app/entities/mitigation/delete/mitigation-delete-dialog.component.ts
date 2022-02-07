import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMitigation } from '../mitigation.model';
import { MitigationService } from '../service/mitigation.service';

@Component({
  templateUrl: './mitigation-delete-dialog.component.html',
})
export class MitigationDeleteDialogComponent {
  mitigation?: IMitigation;

  constructor(protected mitigationService: MitigationService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.mitigationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
