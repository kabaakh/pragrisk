import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';
import { MitigationType } from 'app/entities/enumerations/mitigation-type.model';
import { MitigationStatus } from 'app/entities/enumerations/mitigation-status.model';

export interface IMitigation {
  id?: number;
  controlID?: string;
  title?: string;
  description?: string | null;
  frameworkReference?: string | null;
  type?: MitigationType;
  status?: MitigationStatus;
  vulnerabilities?: IVulnerability[] | null;
}

export class Mitigation implements IMitigation {
  constructor(
    public id?: number,
    public controlID?: string,
    public title?: string,
    public description?: string | null,
    public frameworkReference?: string | null,
    public type?: MitigationType,
    public status?: MitigationStatus,
    public vulnerabilities?: IVulnerability[] | null
  ) {}
}

export function getMitigationIdentifier(mitigation: IMitigation): number | undefined {
  return mitigation.id;
}
