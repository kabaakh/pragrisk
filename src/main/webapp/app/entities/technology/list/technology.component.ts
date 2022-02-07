import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITechnology } from '../technology.model';
import { TechnologyService } from '../service/technology.service';
import { TechnologyDeleteDialogComponent } from '../delete/technology-delete-dialog.component';

@Component({
  selector: 'jhi-technology',
  templateUrl: './technology.component.html',
})
export class TechnologyComponent implements OnInit {
  technologies?: ITechnology[];
  isLoading = false;
  currentSearch: string;

  constructor(protected technologyService: TechnologyService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.technologyService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ITechnology[]>) => {
            this.isLoading = false;
            this.technologies = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.technologyService.query().subscribe({
      next: (res: HttpResponse<ITechnology[]>) => {
        this.isLoading = false;
        this.technologies = res.body ?? [];
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

  trackTechnologyID(index: number, item: ITechnology): string {
    return item.technologyID!;
  }

  delete(technology: ITechnology): void {
    const modalRef = this.modalService.open(TechnologyDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.technology = technology;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
