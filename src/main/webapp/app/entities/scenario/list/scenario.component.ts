import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IScenario } from '../scenario.model';
import { ScenarioService } from '../service/scenario.service';
import { ScenarioDeleteDialogComponent } from '../delete/scenario-delete-dialog.component';

@Component({
  selector: 'jhi-scenario',
  templateUrl: './scenario.component.html',
})
export class ScenarioComponent implements OnInit {
  scenarios?: IScenario[];
  isLoading = false;
  currentSearch: string;

  constructor(protected scenarioService: ScenarioService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.scenarioService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IScenario[]>) => {
            this.isLoading = false;
            this.scenarios = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.scenarioService.query().subscribe({
      next: (res: HttpResponse<IScenario[]>) => {
        this.isLoading = false;
        this.scenarios = res.body ?? [];
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

  trackScenarioID(index: number, item: IScenario): string {
    return item.scenarioID!;
  }

  delete(scenario: IScenario): void {
    const modalRef = this.modalService.open(ScenarioDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.scenario = scenario;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
