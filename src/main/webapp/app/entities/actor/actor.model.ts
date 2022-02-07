import { IScenario } from 'app/entities/scenario/scenario.model';
import { Environment } from 'app/entities/enumerations/environment.model';

export interface IActor {
  actorID?: string;
  firstName?: string;
  lastName?: string;
  nickName?: string;
  environMent?: Environment;
  inheritsFrom?: string | null;
  description?: string | null;
  inheritsFrom?: IActor | null;
  scenarios?: IScenario[] | null;
}

export class Actor implements IActor {
  constructor(
    public actorID?: string,
    public firstName?: string,
    public lastName?: string,
    public nickName?: string,
    public environMent?: Environment,
    public inheritsFrom?: string | null,
    public description?: string | null,
    public inheritsFrom?: IActor | null,
    public scenarios?: IScenario[] | null
  ) {}
}

export function getActorIdentifier(actor: IActor): string | undefined {
  return actor.actorID;
}
