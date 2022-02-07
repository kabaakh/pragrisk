import { IActor } from 'app/entities/actor/actor.model';

export interface IEnvironment {
  id?: number;
  name?: string;
  description?: string | null;
  names?: IActor[] | null;
}

export class Environment implements IEnvironment {
  constructor(public id?: number, public name?: string, public description?: string | null, public names?: IActor[] | null) {}
}

export function getEnvironmentIdentifier(environment: IEnvironment): number | undefined {
  return environment.id;
}
