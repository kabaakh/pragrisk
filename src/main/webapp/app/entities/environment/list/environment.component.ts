import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEnvironment } from '../environment.model';
import { EnvironmentService } from '../service/environment.service';
import { EnvironmentDeleteDialogComponent } from '../delete/environment-delete-dialog.component';

@Component({
  selector: 'jhi-environment',
  templateUrl: './environment.component.html',
})
export class EnvironmentComponent implements OnInit {
  environments?: IEnvironment[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected environmentService: EnvironmentService,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.environmentService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IEnvironment[]>) => {
            this.isLoading = false;
            this.environments = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.environmentService.query().subscribe({
      next: (res: HttpResponse<IEnvironment[]>) => {
        this.isLoading = false;
        this.environments = res.body ?? [];
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

  trackId(index: number, item: IEnvironment): number {
    return item.id!;
  }

  delete(environment: IEnvironment): void {
    const modalRef = this.modalService.open(EnvironmentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.environment = environment;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
