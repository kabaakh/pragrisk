import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITechnology } from '../technology.model';

@Component({
  selector: 'jhi-technology-detail',
  templateUrl: './technology-detail.component.html',
})
export class TechnologyDetailComponent implements OnInit {
  technology: ITechnology | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ technology }) => {
      this.technology = technology;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
