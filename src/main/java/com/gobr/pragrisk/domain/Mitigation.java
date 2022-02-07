package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobr.pragrisk.domain.enumeration.MitigationStatus;
import com.gobr.pragrisk.domain.enumeration.MitigationType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Pattern(regexp = "^R+\\d$")
    @Column(name = "control_id", nullable = false, unique = true)
    private String controlID;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "framework_reference")
    private String frameworkReference;

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

    public Long getId() {
        return this.id;
    }

    public Mitigation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return this.title;
    }

    public Mitigation title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Mitigation description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrameworkReference() {
        return this.frameworkReference;
    }

    public Mitigation frameworkReference(String frameworkReference) {
        this.setFrameworkReference(frameworkReference);
        return this;
    }

    public void setFrameworkReference(String frameworkReference) {
        this.frameworkReference = frameworkReference;
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
        return id != null && id.equals(((Mitigation) o).id);
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
            "id=" + getId() +
            ", controlID='" + getControlID() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", frameworkReference='" + getFrameworkReference() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
