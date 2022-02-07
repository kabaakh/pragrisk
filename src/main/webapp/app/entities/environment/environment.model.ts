export interface IEnvironment {
  id?: number;
  name?: string;
  description?: string | null;
}

export class Environment implements IEnvironment {
  constructor(public id?: number, public name?: string, public description?: string | null) {}
}

export function getEnvironmentIdentifier(environment: IEnvironment): number | undefined {
  return environment.id;
}
