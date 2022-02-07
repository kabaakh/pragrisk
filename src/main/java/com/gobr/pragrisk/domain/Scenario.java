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
    @Column(name = "actor_fk", nullable = false)
    private UUID actorFK;

    @NotNull
    @Column(name = "technology_fk", nullable = false)
    private UUID technologyFK;

    @NotNull
    @Column(name = "vulnerability_fk", nullable = false)
    private UUID vulnerabilityFK;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "probability", precision = 21, scale = 2)
    private BigDecimal probability;

    @Column(name = "qonsequence", precision = 21, scale = 2)
    private BigDecimal qonsequence;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parentAct", "scenarios" }, allowSetters = true)
    private Actor actorFK;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parentTech", "scenarios" }, allowSetters = true)
    private Technology technologyFK;

    @ManyToOne
    @JsonIgnoreProperties(value = { "scenarios", "mitigations" }, allowSetters = true)
    private Vulnerability vulnerabilityFK;

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

    public UUID getActorFK() {
        return this.actorFK;
    }

    public Scenario actorFK(UUID actorFK) {
        this.setActorFK(actorFK);
        return this;
    }

    public void setActorFK(UUID actorFK) {
        this.actorFK = actorFK;
    }

    public UUID getTechnologyFK() {
        return this.technologyFK;
    }

    public Scenario technologyFK(UUID technologyFK) {
        this.setTechnologyFK(technologyFK);
        return this;
    }

    public void setTechnologyFK(UUID technologyFK) {
        this.technologyFK = technologyFK;
    }

    public UUID getVulnerabilityFK() {
        return this.vulnerabilityFK;
    }

    public Scenario vulnerabilityFK(UUID vulnerabilityFK) {
        this.setVulnerabilityFK(vulnerabilityFK);
        return this;
    }

    public void setVulnerabilityFK(UUID vulnerabilityFK) {
        this.vulnerabilityFK = vulnerabilityFK;
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

    public Actor getActorFK() {
        return this.actorFK;
    }

    public void setActorFK(Actor actor) {
        this.actorFK = actor;
    }

    public Scenario actorFK(Actor actor) {
        this.setActorFK(actor);
        return this;
    }

    public Technology getTechnologyFK() {
        return this.technologyFK;
    }

    public void setTechnologyFK(Technology technology) {
        this.technologyFK = technology;
    }

    public Scenario technologyFK(Technology technology) {
        this.setTechnologyFK(technology);
        return this;
    }

    public Vulnerability getVulnerabilityFK() {
        return this.vulnerabilityFK;
    }

    public void setVulnerabilityFK(Vulnerability vulnerability) {
        this.vulnerabilityFK = vulnerability;
    }

    public Scenario vulnerabilityFK(Vulnerability vulnerability) {
        this.setVulnerabilityFK(vulnerability);
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
            ", actorFK='" + getActorFK() + "'" +
            ", technologyFK='" + getTechnologyFK() + "'" +
            ", vulnerabilityFK='" + getVulnerabilityFK() + "'" +
            ", description='" + getDescription() + "'" +
            ", probability=" + getProbability() +
            ", qonsequence=" + getQonsequence() +
            "}";
    }
}
