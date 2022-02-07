import { IScenario } from 'app/entities/scenario/scenario.model';
import { TechCategory } from 'app/entities/enumerations/tech-category.model';
import { TechStack } from 'app/entities/enumerations/tech-stack.model';

export interface ITechnology {
  id?: number;
  name?: string;
  category?: TechCategory;
  description?: string | null;
  techStackType?: TechStack;
  parentTechnology?: ITechnology | null;
  technologyIDS?: IScenario[] | null;
}

export class Technology implements ITechnology {
  constructor(
    public id?: number,
    public name?: string,
    public category?: TechCategory,
    public description?: string | null,
    public techStackType?: TechStack,
    public parentTechnology?: ITechnology | null,
    public technologyIDS?: IScenario[] | null
  ) {}
}

export function getTechnologyIdentifier(technology: ITechnology): number | undefined {
  return technology.id;
}
