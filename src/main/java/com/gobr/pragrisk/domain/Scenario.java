package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Scenario.
 */
@Entity
@Table(name = "scenario")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "scenario")
public class Scenario implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioID;

    @NotNull
    @Column(name = "actor_id", nullable = false)
    private UUID actorID;

    @NotNull
    @Column(name = "technology_id", nullable = false)
    private UUID technologyID;

    @NotNull
    @Column(name = "vulnerability_id", nullable = false)
    private UUID vulnerabilityID;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "probability", precision = 21, scale = 2)
    private BigDecimal probability;

    @Column(name = "qonsequence", precision = 21, scale = 2)
    private BigDecimal qonsequence;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inheritsFrom", "scenarios" }, allowSetters = true)
    private Actor actorID;

    @ManyToOne
    @JsonIgnoreProperties(value = { "inheritsFrom", "scenarios" }, allowSetters = true)
    private Technology technologyID;

    @ManyToOne
    @JsonIgnoreProperties(value = { "scenarios", "mitigations" }, allowSetters = true)
    private Vulnerability vulnerabilityID;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getScenarioID() {
        return this.scenarioID;
    }

    public Scenario scenarioID(UUID scenarioID) {
        this.setScenarioID(scenarioID);
        return this;
    }

    public void setScenarioID(UUID scenarioID) {
        this.scenarioID = scenarioID;
    }

    public UUID getActorID() {
        return this.actorID;
    }

    public Scenario actorID(UUID actorID) {
        this.setActorID(actorID);
        return this;
    }

    public void setActorID(UUID actorID) {
        this.actorID = actorID;
    }

    public UUID getTechnologyID() {
        return this.technologyID;
    }

    public Scenario technologyID(UUID technologyID) {
        this.setTechnologyID(technologyID);
        return this;
    }

    public void setTechnologyID(UUID technologyID) {
        this.technologyID = technologyID;
    }

    public UUID getVulnerabilityID() {
        return this.vulnerabilityID;
    }

    public Scenario vulnerabilityID(UUID vulnerabilityID) {
        this.setVulnerabilityID(vulnerabilityID);
        return this;
    }

    public void setVulnerabilityID(UUID vulnerabilityID) {
        this.vulnerabilityID = vulnerabilityID;
    }

    public String getDescription() {
        return this.description;
    }

    public Scenario description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getProbability() {
        return this.probability;
    }

    public Scenario probability(BigDecimal probability) {
        this.setProbability(probability);
        return this;
    }

    public void setProbability(BigDecimal probability) {
        this.probability = probability;
    }

    public BigDecimal getQonsequence() {
        return this.qonsequence;
    }

    public Scenario qonsequence(BigDecimal qonsequence) {
        this.setQonsequence(qonsequence);
        return this;
    }

    public void setQonsequence(BigDecimal qonsequence) {
        this.qonsequence = qonsequence;
    }

    public Actor getActorID() {
        return this.actorID;
    }

    public void setActorID(Actor actor) {
        this.actorID = actor;
    }

    public Scenario actorID(Actor actor) {
        this.setActorID(actor);
        return this;
    }

    public Technology getTechnologyID() {
        return this.technologyID;
    }

    public void setTechnologyID(Technology technology) {
        this.technologyID = technology;
    }

    public Scenario technologyID(Technology technology) {
        this.setTechnologyID(technology);
        return this;
    }

    public Vulnerability getVulnerabilityID() {
        return this.vulnerabilityID;
    }

    public void setVulnerabilityID(Vulnerability vulnerability) {
        this.vulnerabilityID = vulnerability;
    }

    public Scenario vulnerabilityID(Vulnerability vulnerability) {
        this.setVulnerabilityID(vulnerability);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scenario)) {
            return false;
        }
        return scenarioID != null && scenarioID.equals(((Scenario) o).scenarioID);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Scenario{" +
            "scenarioID=" + getScenarioID() +
            ", actorID='" + getActorID() + "'" +
            ", technologyID='" + getTechnologyID() + "'" +
            ", vulnerabilityID='" + getVulnerabilityID() + "'" +
            ", description='" + getDescription() + "'" +
            ", probability=" + getProbability() +
            ", qonsequence=" + getQonsequence() +
            "}";
    }
}
