package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobr.pragrisk.domain.enumeration.MitigationStatus;
import com.gobr.pragrisk.domain.enumeration.MitigationType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Mitigation.
 */
@Entity
@Table(name = "mitigation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mitigation")
public class Mitigation implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Column(name = "mitigation_id", nullable = false, unique = true)
    private UUID mitigationID;

    @NotNull
    @Column(name = "control_id", nullable = false)
    private String controlID;

    @Column(name = "reference")
    private String reference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MitigationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MitigationStatus status;

    @ManyToMany(mappedBy = "mitigations")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "vulnerabilityIDS", "mitigations" }, allowSetters = true)
    private Set<Vulnerability> vulnerabilities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getMitigationID() {
        return this.mitigationID;
    }

    public Mitigation mitigationID(UUID mitigationID) {
        this.setMitigationID(mitigationID);
        return this;
    }

    public void setMitigationID(UUID mitigationID) {
        this.mitigationID = mitigationID;
    }

    public String getControlID() {
        return this.controlID;
    }

    public Mitigation controlID(String controlID) {
        this.setControlID(controlID);
        return this;
    }

    public void setControlID(String controlID) {
        this.controlID = controlID;
    }

    public String getReference() {
        return this.reference;
    }

    public Mitigation reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public MitigationType getType() {
        return this.type;
    }

    public Mitigation type(MitigationType type) {
        this.setType(type);
        return this;
    }

    public void setType(MitigationType type) {
        this.type = type;
    }

    public MitigationStatus getStatus() {
        return this.status;
    }

    public Mitigation status(MitigationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(MitigationStatus status) {
        this.status = status;
    }

    public Set<Vulnerability> getVulnerabilities() {
        return this.vulnerabilities;
    }

    public void setVulnerabilities(Set<Vulnerability> vulnerabilities) {
        if (this.vulnerabilities != null) {
            this.vulnerabilities.forEach(i -> i.removeMitigation(this));
        }
        if (vulnerabilities != null) {
            vulnerabilities.forEach(i -> i.addMitigation(this));
        }
        this.vulnerabilities = vulnerabilities;
    }

    public Mitigation vulnerabilities(Set<Vulnerability> vulnerabilities) {
        this.setVulnerabilities(vulnerabilities);
        return this;
    }

    public Mitigation addVulnerability(Vulnerability vulnerability) {
        this.vulnerabilities.add(vulnerability);
        vulnerability.getMitigations().add(this);
        return this;
    }

    public Mitigation removeVulnerability(Vulnerability vulnerability) {
        this.vulnerabilities.remove(vulnerability);
        vulnerability.getMitigations().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mitigation)) {
            return false;
        }
        return mitigationID != null && mitigationID.equals(((Mitigation) o).mitigationID);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mitigation{" +
            "mitigationID=" + getMitigationID() +
            ", controlID='" + getControlID() + "'" +
            ", reference='" + getReference() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
