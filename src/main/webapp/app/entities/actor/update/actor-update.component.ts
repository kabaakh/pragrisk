import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IActor, Actor } from '../actor.model';
import { ActorService } from '../service/actor.service';
import { IEnvironment } from 'app/entities/environment/environment.model';
import { EnvironmentService } from 'app/entities/environment/service/environment.service';

@Component({
  selector: 'jhi-actor-update',
  templateUrl: './actor-update.component.html',
})
export class ActorUpdateComponent implements OnInit {
  isSaving = false;

  parentActorsCollection: IActor[] = [];
  environmentsSharedCollection: IEnvironment[] = [];

  editForm = this.fb.group({
    id: [],
    firstName: [null, [Validators.required]],
    lastName: [null, [Validators.required]],
    nickName: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    parentActor: [],
    group: [],
  });

  constructor(
    protected actorService: ActorService,
    protected environmentService: EnvironmentService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ actor }) => {
      this.updateForm(actor);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const actor = this.createFromForm();
    if (actor.id !== undefined) {
      this.subscribeToSaveResponse(this.actorService.update(actor));
    } else {
      this.subscribeToSaveResponse(this.actorService.create(actor));
    }
  }

  trackActorById(index: number, item: IActor): number {
    return item.id!;
  }

  trackEnvironmentById(index: number, item: IEnvironment): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActor>>): void {
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

  protected updateForm(actor: IActor): void {
    this.editForm.patchValue({
      id: actor.id,
      firstName: actor.firstName,
      lastName: actor.lastName,
      nickName: actor.nickName,
      description: actor.description,
      parentActor: actor.parentActor,
      group: actor.group,
    });

    this.parentActorsCollection = this.actorService.addActorToCollectionIfMissing(this.parentActorsCollection, actor.parentActor);
    this.environmentsSharedCollection = this.environmentService.addEnvironmentToCollectionIfMissing(
      this.environmentsSharedCollection,
      actor.group
    );
  }

  protected loadRelationshipsOptions(): void {
    this.actorService
      .query({ filter: 'actor-is-null' })
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing(actors, this.editForm.get('parentActor')!.value)))
      .subscribe((actors: IActor[]) => (this.parentActorsCollection = actors));

    this.environmentService
      .query()
      .pipe(map((res: HttpResponse<IEnvironment[]>) => res.body ?? []))
      .pipe(
        map((environments: IEnvironment[]) =>
          this.environmentService.addEnvironmentToCollectionIfMissing(environments, this.editForm.get('group')!.value)
        )
      )
      .subscribe((environments: IEnvironment[]) => (this.environmentsSharedCollection = environments));
  }

  protected createFromForm(): IActor {
    return {
      ...new Actor(),
      id: this.editForm.get(['id'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      nickName: this.editForm.get(['nickName'])!.value,
      description: this.editForm.get(['description'])!.value,
      parentActor: this.editForm.get(['parentActor'])!.value,
      group: this.editForm.get(['group'])!.value,
    };
  }
}
