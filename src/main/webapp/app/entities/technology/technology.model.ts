import { IScenario } from 'app/entities/scenario/scenario.model';
import { TechCategory } from 'app/entities/enumerations/tech-category.model';
import { TechStack } from 'app/entities/enumerations/tech-stack.model';

export interface ITechnology {
  technologyID?: string;
  name?: string;
  category?: TechCategory;
  description?: string | null;
  parentTechnology?: string | null;
  techStackType?: TechStack | null;
  parentTechnology?: ITechnology | null;
  technologyIDS?: IScenario[] | null;
}

export class Technology implements ITechnology {
  constructor(
    public technologyID?: string,
    public name?: string,
    public category?: TechCategory,
    public description?: string | null,
    public parentTechnology?: string | null,
    public techStackType?: TechStack | null,
    public parentTechnology?: ITechnology | null,
    public technologyIDS?: IScenario[] | null
  ) {}
}

export function getTechnologyIdentifier(technology: ITechnology): string | undefined {
  return technology.technologyID;
}
