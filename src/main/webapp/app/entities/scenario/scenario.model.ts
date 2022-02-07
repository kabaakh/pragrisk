import { IActor } from 'app/entities/actor/actor.model';
import { ITechnology } from 'app/entities/technology/technology.model';
import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';

export interface IScenario {
  scenarioID?: string;
  actorID?: string;
  technologyID?: string;
  vulnerabilityID?: string;
  description?: string | null;
  probability?: number | null;
  qonsequence?: number | null;
  actorID?: IActor | null;
  technologyID?: ITechnology | null;
  vulnerabilityID?: IVulnerability | null;
}

export class Scenario implements IScenario {
  constructor(
    public scenarioID?: string,
    public actorID?: string,
    public technologyID?: string,
    public vulnerabilityID?: string,
    public description?: string | null,
    public probability?: number | null,
    public qonsequence?: number | null,
    public actorID?: IActor | null,
    public technologyID?: ITechnology | null,
    public vulnerabilityID?: IVulnerability | null
  ) {}
}

export function getScenarioIdentifier(scenario: IScenario): string | undefined {
  return scenario.scenarioID;
}
