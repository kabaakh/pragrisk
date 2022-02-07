import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IActor } from '../actor.model';
import { ActorService } from '../service/actor.service';

@Component({
  templateUrl: './actor-delete-dialog.component.html',
})
export class ActorDeleteDialogComponent {
  actor?: IActor;

  constructor(protected actorService: ActorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.actorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
