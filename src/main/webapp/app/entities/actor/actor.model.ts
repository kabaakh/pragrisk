import { IScenario } from 'app/entities/scenario/scenario.model';
import { Environment } from 'app/entities/enumerations/environment.model';

export interface IActor {
  actorID?: string;
  firstName?: string;
  lastName?: string;
  nickName?: string;
  group?: Environment;
  description?: string | null;
  parentActor?: IActor | null;
  actorIDS?: IScenario[] | null;
}

export class Actor implements IActor {
  constructor(
    public actorID?: string,
    public firstName?: string,
    public lastName?: string,
    public nickName?: string,
    public group?: Environment,
    public description?: string | null,
    public parentActor?: IActor | null,
    public actorIDS?: IScenario[] | null
  ) {}
}

export function getActorIdentifier(actor: IActor): string | undefined {
  return actor.actorID;
}
