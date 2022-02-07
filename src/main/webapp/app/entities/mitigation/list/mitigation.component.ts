import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMitigation } from '../mitigation.model';
import { MitigationService } from '../service/mitigation.service';
import { MitigationDeleteDialogComponent } from '../delete/mitigation-delete-dialog.component';

@Component({
  selector: 'jhi-mitigation',
  templateUrl: './mitigation.component.html',
})
export class MitigationComponent implements OnInit {
  mitigations?: IMitigation[];
  isLoading = false;
  currentSearch: string;

  constructor(protected mitigationService: MitigationService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.mitigationService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IMitigation[]>) => {
            this.isLoading = false;
            this.mitigations = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.mitigationService.query().subscribe({
      next: (res: HttpResponse<IMitigation[]>) => {
        this.isLoading = false;
        this.mitigations = res.body ?? [];
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

  trackVulnerabiltyID(index: number, item: IMitigation): string {
    return item.vulnerabiltyID!;
  }

  delete(mitigation: IMitigation): void {
    const modalRef = this.modalService.open(MitigationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.mitigation = mitigation;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
