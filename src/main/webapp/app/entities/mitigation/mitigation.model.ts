import { IVulnerability } from 'app/entities/vulnerability/vulnerability.model';
import { MitigationType } from 'app/entities/enumerations/mitigation-type.model';
import { MitigationStatus } from 'app/entities/enumerations/mitigation-status.model';

export interface IMitigation {
  mitigationID?: string;
  controlID?: string;
  reference?: string | null;
  type?: MitigationType;
  status?: MitigationStatus;
  vulnerabilities?: IVulnerability[] | null;
}

export class Mitigation implements IMitigation {
  constructor(
    public mitigationID?: string,
    public controlID?: string,
    public reference?: string | null,
    public type?: MitigationType,
    public status?: MitigationStatus,
    public vulnerabilities?: IVulnerability[] | null
  ) {}
}

export function getMitigationIdentifier(mitigation: IMitigation): string | undefined {
  return mitigation.mitigationID;
}
