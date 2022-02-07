import { IScenario } from 'app/entities/scenario/scenario.model';
import { IEnvironment } from 'app/entities/environment/environment.model';

export interface IActor {
  actorID?: string;
  firstName?: string;
  lastName?: string;
  nickName?: string;
  environment?: string;
  description?: string | null;
  parentActor?: IActor | null;
  actorIDS?: IScenario[] | null;
  environment?: IEnvironment | null;
}

export class Actor implements IActor {
  constructor(
    public actorID?: string,
    public firstName?: string,
    public lastName?: string,
    public nickName?: string,
    public environment?: string,
    public description?: string | null,
    public parentActor?: IActor | null,
    public actorIDS?: IScenario[] | null,
    public environment?: IEnvironment | null
  ) {}
}

export function getActorIdentifier(actor: IActor): string | undefined {
  return actor.actorID;
}
