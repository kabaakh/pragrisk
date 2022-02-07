import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IActor, Actor } from '../actor.model';
import { ActorService } from '../service/actor.service';
import { Environment } from 'app/entities/enumerations/environment.model';

@Component({
  selector: 'jhi-actor-update',
  templateUrl: './actor-update.component.html',
})
export class ActorUpdateComponent implements OnInit {
  isSaving = false;
  environmentValues = Object.keys(Environment);

  parentActorsCollection: IActor[] = [];

  editForm = this.fb.group({
    actorID: [null, [Validators.required]],
    firstName: [null, [Validators.required]],
    lastName: [null, [Validators.required]],
    nickName: [null, [Validators.required]],
    group: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    parentActor: [],
  });

  constructor(protected actorService: ActorService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

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
    if (actor.actorID !== undefined) {
      this.subscribeToSaveResponse(this.actorService.update(actor));
    } else {
      this.subscribeToSaveResponse(this.actorService.create(actor));
    }
  }

  trackActorByActorID(index: number, item: IActor): string {
    return item.actorID!;
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
      actorID: actor.actorID,
      firstName: actor.firstName,
      lastName: actor.lastName,
      nickName: actor.nickName,
      group: actor.group,
      description: actor.description,
      parentActor: actor.parentActor,
    });

    this.parentActorsCollection = this.actorService.addActorToCollectionIfMissing(this.parentActorsCollection, actor.parentActor);
  }

  protected loadRelationshipsOptions(): void {
    this.actorService
      .query({ filter: 'actor-is-null' })
      .pipe(map((res: HttpResponse<IActor[]>) => res.body ?? []))
      .pipe(map((actors: IActor[]) => this.actorService.addActorToCollectionIfMissing(actors, this.editForm.get('parentActor')!.value)))
      .subscribe((actors: IActor[]) => (this.parentActorsCollection = actors));
  }

  protected createFromForm(): IActor {
    return {
      ...new Actor(),
      actorID: this.editForm.get(['actorID'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      nickName: this.editForm.get(['nickName'])!.value,
      group: this.editForm.get(['group'])!.value,
      description: this.editForm.get(['description'])!.value,
      parentActor: this.editForm.get(['parentActor'])!.value,
    };
  }
}
