import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'actor',
        data: { pageTitle: 'pragRiskApp.actor.home.title' },
        loadChildren: () => import('./actor/actor.module').then(m => m.ActorModule),
      },
      {
        path: 'environment',
        data: { pageTitle: 'pragRiskApp.environment.home.title' },
        loadChildren: () => import('./environment/environment.module').then(m => m.EnvironmentModule),
      },
      {
        path: 'scenario',
        data: { pageTitle: 'pragRiskApp.scenario.home.title' },
        loadChildren: () => import('./scenario/scenario.module').then(m => m.ScenarioModule),
      },
      {
        path: 'vulnerability',
        data: { pageTitle: 'pragRiskApp.vulnerability.home.title' },
        loadChildren: () => import('./vulnerability/vulnerability.module').then(m => m.VulnerabilityModule),
      },
      {
        path: 'technology',
        data: { pageTitle: 'pragRiskApp.technology.home.title' },
        loadChildren: () => import('./technology/technology.module').then(m => m.TechnologyModule),
      },
      {
        path: 'mitigation',
        data: { pageTitle: 'pragRiskApp.mitigation.home.title' },
        loadChildren: () => import('./mitigation/mitigation.module').then(m => m.MitigationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
