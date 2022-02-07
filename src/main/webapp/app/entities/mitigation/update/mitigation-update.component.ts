import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMitigation, Mitigation } from '../mitigation.model';
import { MitigationService } from '../service/mitigation.service';
import { MitigationType } from 'app/entities/enumerations/mitigation-type.model';
import { MitigationStatus } from 'app/entities/enumerations/mitigation-status.model';

@Component({
  selector: 'jhi-mitigation-update',
  templateUrl: './mitigation-update.component.html',
})
export class MitigationUpdateComponent implements OnInit {
  isSaving = false;
  mitigationTypeValues = Object.keys(MitigationType);
  mitigationStatusValues = Object.keys(MitigationStatus);

  editForm = this.fb.group({
    vulnerabiltyID: [null, [Validators.required]],
    controlID: [null, [Validators.required]],
    reference: [],
    type: [null, [Validators.required]],
    status: [null, [Validators.required]],
  });

  constructor(protected mitigationService: MitigationService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mitigation }) => {
      this.updateForm(mitigation);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mitigation = this.createFromForm();
    if (mitigation.vulnerabiltyID !== undefined) {
      this.subscribeToSaveResponse(this.mitigationService.update(mitigation));
    } else {
      this.subscribeToSaveResponse(this.mitigationService.create(mitigation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMitigation>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(mitigation: IMitigation): void {
    this.editForm.patchValue({
      vulnerabiltyID: mitigation.vulnerabiltyID,
      controlID: mitigation.controlID,
      reference: mitigation.reference,
      type: mitigation.type,
      status: mitigation.status,
    });
  }

  protected createFromForm(): IMitigation {
    return {
      ...new Mitigation(),
      vulnerabiltyID: this.editForm.get(['vulnerabiltyID'])!.value,
      controlID: this.editForm.get(['controlID'])!.value,
      reference: this.editForm.get(['reference'])!.value,
      type: this.editForm.get(['type'])!.value,
      status: this.editForm.get(['status'])!.value,
    };
  }
}
