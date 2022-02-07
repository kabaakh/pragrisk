import { IScenario } from 'app/entities/scenario/scenario.model';
import { IEnvironment } from 'app/entities/environment/environment.model';

export interface IActor {
  id?: number;
  firstName?: string;
  lastName?: string;
  nickName?: string;
  group?: string;
  description?: string | null;
  parentActor?: IActor | null;
  actorIDS?: IScenario[] | null;
  group?: IEnvironment | null;
}

export class Actor implements IActor {
  constructor(
    public id?: number,
    public firstName?: string,
    public lastName?: string,
    public nickName?: string,
    public group?: string,
    public description?: string | null,
    public parentActor?: IActor | null,
    public actorIDS?: IScenario[] | null,
    public group?: IEnvironment | null
  ) {}
}

export function getActorIdentifier(actor: IActor): number | undefined {
  return actor.id;
}
