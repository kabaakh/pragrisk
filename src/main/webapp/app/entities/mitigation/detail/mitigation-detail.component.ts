import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMitigation } from '../mitigation.model';

@Component({
  selector: 'jhi-mitigation-detail',
  templateUrl: './mitigation-detail.component.html',
})
export class MitigationDetailComponent implements OnInit {
  mitigation: IMitigation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mitigation }) => {
      this.mitigation = mitigation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
