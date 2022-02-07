import { IActor } from 'app/entities/actor/actor.model';
import { ITechnology } from 'app/entities/technology/technology.model';
import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';

export interface IScenario {
  id?: number;
  title?: string;
  description?: string | null;
  probability?: number | null;
  qonsequence?: number | null;
  riskValue?: number | null;
  actorFK?: IActor | null;
  technologyFK?: ITechnology | null;
  vulnerabilityFK?: IVulnerability | null;
}

export class Scenario implements IScenario {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string | null,
    public probability?: number | null,
    public qonsequence?: number | null,
    public riskValue?: number | null,
    public actorFK?: IActor | null,
    public technologyFK?: ITechnology | null,
    public vulnerabilityFK?: IVulnerability | null
  ) {}
}

export function getScenarioIdentifier(scenario: IScenario): number | undefined {
  return scenario.id;
}
