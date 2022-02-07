import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IEnvironment, Environment } from '../environment.model';
import { EnvironmentService } from '../service/environment.service';

@Component({
  selector: 'jhi-environment-update',
  templateUrl: './environment-update.component.html',
})
export class EnvironmentUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
  });

  constructor(protected environmentService: EnvironmentService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ environment }) => {
      this.updateForm(environment);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const environment = this.createFromForm();
    if (environment.id !== undefined) {
      this.subscribeToSaveResponse(this.environmentService.update(environment));
    } else {
      this.subscribeToSaveResponse(this.environmentService.create(environment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnvironment>>): void {
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

  protected updateForm(environment: IEnvironment): void {
    this.editForm.patchValue({
      id: environment.id,
      name: environment.name,
      description: environment.description,
    });
  }

  protected createFromForm(): IEnvironment {
    return {
      ...new Environment(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
