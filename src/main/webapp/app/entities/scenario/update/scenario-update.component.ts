import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IScenario, Scenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';
import { IActor } from 'app/entities/actor/actor.model';
import { ActorService } from 'app/entities/actor/service/actor.service';
import { ITechnology } from 'app/entities/technology/technology.model';
import { TechnologyService } from 'app/entities/technology/service/technology.service';
import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';
import { VulnerabilityService } from 'app/entities/vulnerability/service/vulnerability.service';

@Component({
  selector: 'jhi-scenario-update',
  templateUrl: './scenario-update.component.html',
})
export class ScenarioUpdateComponent implements OnInit {
  isSaving = false;

  actorsSharedCollection: IActor[] = [];
  technologiesSharedCollection: ITechnology[] = [];
  vulnerabilitiesSharedCollection: IVulnerability[] = [];

  editForm = this.fb.group({
    scenarioID: [null, [Validators.required]],
    actorID: [null, [Validators.required]],
    technologyID: [null, [Validators.required]],
    vulnerabilityID: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    probability: [],
    qonsequence: [],
    actorID: [],
    technologyID: [],
    vulnerabilityID: [],
  });

  constructor(
    protected scenarioService: ScenarioService,
    protected actorService: ActorService,
    protected technologyService: TechnologyService,
    protected vulnerabilityService: VulnerabilityService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ scenario }) => {
      this.updateForm(scenario);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const scenario = this.createFromForm();
    if (scenario.scenarioID !== undefined) {
      this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    } else {
      this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    }
  }

  trackActorByActorID(index: number, item: IActor): string {
    return item.actorID!;
  }

  trackTechnologyByTechnologyID(index: number, item: ITechnology): string {
    return item.technologyID!;
  }

  trackVulnerabilityByVulnerabilityID(index: number, item: IVulnerability): string {
    return item.vulnerabilityID!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScenario>>): void {
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

  protected updateForm(scenario: IScenario): void {
    this.editForm.patchValue({
      scenarioID: scenario.scenarioID,
      actorID: scenario.actorID,
      technologyID: scenario.technologyID,
      vulnerabilityID: scenario.vulnerabilityID,
      description: scenario.description,
      probability: scenario.probability,
      qonsequence: scenario.qonsequence,
      actorID: scenario.actorID,
      technologyID: scenario.technologyID,
      vulnerabilityID: scenario.vulnerabilityID,
    });

    this.actorsSharedCollection = this.actorService.addActorToCollectionIfMissing(this.actorsSharedCollection, scenario.actorID);
    this.technologiesSharedCollection = this.technologyService.addTechnologyToCollectionIfMissing(
      this.technologiesSharedCollection,
      scenario.technologyID
    );
    this.vulnerabilitiesSharedCollection = this.vulnerabilityService.addVulnerabilityToCollectionIfMissing(
      this.vulnerabilitiesSharedCollection,
      scenario.vulnerabilityID
    );
  }

  protected loadRelationshipsOptions(): void {
    this.actorService
      .query()
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing(actors, this.editForm.get('actorID')!.value)))
      .subscribe((actors: IActor[]) => (this.actorsSharedCollection = actors));

    this.technologyService
      .query()
      .pipe(map((res: HttpResponse<ITechnology[]>) => res.body ?? []))
      .pipe(
        map((technologies: ITechnology[]) =>
          this.technologyService.addTechnologyToCollectionIfMissing(technologies, this.editForm.get('technologyID')!.value)
        )
      )
      .subscribe((technologies: ITechnology[]) => (this.technologiesSharedCollection = technologies));

    this.vulnerabilityService
      .query()
      .pipe(map((res: HttpResponse<IVulnerability[]>) => res.body ?? []))
      .pipe(
        map((vulnerabilities: IVulnerability[]) =>
          this.vulnerabilityService.addVulnerabilityToCollectionIfMissing(vulnerabilities, this.editForm.get('vulnerabilityID')!.value)
        )
      )
      .subscribe((vulnerabilities: IVulnerability[]) => (this.vulnerabilitiesSharedCollection = vulnerabilities));
  }

  protected createFromForm(): IScenario {
    return {
      ...new Scenario(),
      scenarioID: this.editForm.get(['scenarioID'])!.value,
      actorID: this.editForm.get(['actorID'])!.value,
      technologyID: this.editForm.get(['technologyID'])!.value,
      vulnerabilityID: this.editForm.get(['vulnerabilityID'])!.value,
      description: this.editForm.get(['description'])!.value,
      probability: this.editForm.get(['probability'])!.value,
      qonsequence: this.editForm.get(['qonsequence'])!.value,
      actorID: this.editForm.get(['actorID'])!.value,
      technologyID: this.editForm.get(['technologyID'])!.value,
      vulnerabilityID: this.editForm.get(['vulnerabilityID'])!.value,
    };
  }
}
