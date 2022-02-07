import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IActor } from '../actor.model';
import { ActorService } from '../service/actor.service';
import { ActorDeleteDialogComponent } from '../delete/actor-delete-dialog.component';

@Component({
  selector: 'jhi-actor',
  templateUrl: './actor.component.html',
})
export class ActorComponent implements OnInit {
  actors?: IActor[];
  isLoading = false;
  currentSearch: string;

  constructor(protected actorService: ActorService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.actorService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IActor[]>) => {
            this.isLoading = false;
            this.actors = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.actorService.query().subscribe({
      next: (res: HttpResponse<IActor[]>) => {
        this.isLoading = false;
        this.actors = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackActorID(index: number, item: IActor): string {
    return item.actorID!;
  }

  delete(actor: IActor): void {
    const modalRef = this.modalService.open(ActorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.actor = actor;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
