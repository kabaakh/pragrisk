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
    id: [],
    title: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    probability: [],
    qonsequence: [],
    riskValue: [],
    actorFK: [],
    technologyFK: [],
    vulnerabilityFK: [],
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
    if (scenario.id !== undefined) {
      this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    } else {
      this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    }
  }

  trackActorById(index: number, item: IActor): number {
    return item.id!;
  }

  trackTechnologyById(index: number, item: ITechnology): number {
    return item.id!;
  }

  trackVulnerabilityById(index: number, item: IVulnerability): number {
    return item.id!;
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
      id: scenario.id,
      title: scenario.title,
      description: scenario.description,
      probability: scenario.probability,
      qonsequence: scenario.qonsequence,
      riskValue: scenario.riskValue,
      actorFK: scenario.actorFK,
      technologyFK: scenario.technologyFK,
      vulnerabilityFK: scenario.vulnerabilityFK,
    });

    this.actorsSharedCollection = this.actorService.addActorToCollectionIfMissing(this.actorsSharedCollection, scenario.actorFK);
    this.technologiesSharedCollection = this.technologyService.addTechnologyToCollectionIfMissing(
      this.technologiesSharedCollection,
      scenario.technologyFK
    );
    this.vulnerabilitiesSharedCollection = this.vulnerabilityService.addVulnerabilityToCollectionIfMissing(
      this.vulnerabilitiesSharedCollection,
      scenario.vulnerabilityFK
    );
  }

  protected loadRelationshipsOptions(): void {
    this.actorService
      .query()
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing(actors, this.editForm.get('actorFK')!.value)))
      .subscribe((actors: IActor[]) => (this.actorsSharedCollection = actors));

    this.technologyService
      .query()
      .pipe(map((res: HttpResponse<ITechnology[]>) => res.body ?? []))
      .pipe(
        map((technologies: ITechnology[]) =>
          this.technologyService.addTechnologyToCollectionIfMissing(technologies, this.editForm.get('technologyFK')!.value)
        )
      )
      .subscribe((technologies: ITechnology[]) => (this.technologiesSharedCollection = technologies));

    this.vulnerabilityService
      .query()
      .pipe(map((res: HttpResponse<IVulnerability[]>) => res.body ?? []))
      .pipe(
        map((vulnerabilities: IVulnerability[]) =>
          this.vulnerabilityService.addVulnerabilityToCollectionIfMissing(vulnerabilities, this.editForm.get('vulnerabilityFK')!.value)
        )
      )
      .subscribe((vulnerabilities: IVulnerability[]) => (this.vulnerabilitiesSharedCollection = vulnerabilities));
  }

  protected createFromForm(): IScenario {
    return {
      ...new Scenario(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      probability: this.editForm.get(['probability'])!.value,
      qonsequence: this.editForm.get(['qonsequence'])!.value,
      riskValue: this.editForm.get(['riskValue'])!.value,
      actorFK: this.editForm.get(['actorFK'])!.value,
      technologyFK: this.editForm.get(['technologyFK'])!.value,
      vulnerabilityFK: this.editForm.get(['vulnerabilityFK'])!.value,
    };
  }
}
