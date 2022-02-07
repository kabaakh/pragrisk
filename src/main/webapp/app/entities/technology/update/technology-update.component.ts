import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITechnology, Technology } from '../technology.model';
import { TechnologyService } from '../service/technology.service';
import { TechCategory } from 'app/entities/enumerations/tech-category.model';
import { TechStack } from 'app/entities/enumerations/tech-stack.model';

@Component({
  selector: 'jhi-technology-update',
  templateUrl: './technology-update.component.html',
})
export class TechnologyUpdateComponent implements OnInit {
  isSaving = false;
  techCategoryValues = Object.keys(TechCategory);
  techStackValues = Object.keys(TechStack);

  parentTechnologiesCollection: ITechnology[] = [];

  editForm = this.fb.group({
    technologyID: [null, [Validators.required]],
    name: [null, [Validators.required]],
    category: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    techStackType: [],
    parentTechnology: [],
  });

  constructor(protected technologyService: TechnologyService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ technology }) => {
      this.updateForm(technology);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const technology = this.createFromForm();
    if (technology.technologyID !== undefined) {
      this.subscribeToSaveResponse(this.technologyService.update(technology));
    } else {
      this.subscribeToSaveResponse(this.technologyService.create(technology));
    }
  }

  trackTechnologyByTechnologyID(index: number, item: ITechnology): string {
    return item.technologyID!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITechnology>>): void {
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

  protected updateForm(technology: ITechnology): void {
    this.editForm.patchValue({
      technologyID: technology.technologyID,
      name: technology.name,
      category: technology.category,
      description: technology.description,
      techStackType: technology.techStackType,
      parentTechnology: technology.parentTechnology,
    });

    this.parentTechnologiesCollection = this.technologyService.addTechnologyToCollectionIfMissing(
      this.parentTechnologiesCollection,
      technology.parentTechnology
    );
  }

  protected loadRelationshipsOptions(): void {
    this.technologyService
      .query({ filter: 'technology-is-null' })
      .pipe(map((res: HttpResponse<ITechnology[]>) => res.body ?? []))
      .pipe(
        map((technologies: ITechnology[]) =>
          this.technologyService.addTechnologyToCollectionIfMissing(technologies, this.editForm.get('parentTechnology')!.value)
        )
      )
      .subscribe((technologies: ITechnology[]) => (this.parentTechnologiesCollection = technologies));
  }

  protected createFromForm(): ITechnology {
    return {
      ...new Technology(),
      technologyID: this.editForm.get(['technologyID'])!.value,
      name: this.editForm.get(['name'])!.value,
      category: this.editForm.get(['category'])!.value,
      description: this.editForm.get(['description'])!.value,
      techStackType: this.editForm.get(['techStackType'])!.value,
      parentTechnology: this.editForm.get(['parentTechnology'])!.value,
    };
  }
}
