package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobr.pragrisk.domain.enumeration.TechCategory;
import com.gobr.pragrisk.domain.enumeration.TechStack;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Technology.
 */
@Entity
@Table(name = "technology")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "technology")
public class Technology implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Column(name = "technology_id", nullable = false, unique = true)
    private UUID technologyID;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TechCategory category;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "inherits_from")
    private UUID inheritsFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "tech_stack_type")
    private TechStack techStackType;

    @JsonIgnoreProperties(value = { "inheritsFrom", "scenarios" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Technology inheritsFrom;

    @OneToMany(mappedBy = "technologyID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "actorID", "technologyID", "vulnerabilityID" }, allowSetters = true)
    private Set<Scenario> scenarios = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getTechnologyID() {
        return this.technologyID;
    }

    public Technology technologyID(UUID technologyID) {
        this.setTechnologyID(technologyID);
        return this;
    }

    public void setTechnologyID(UUID technologyID) {
        this.technologyID = technologyID;
    }

    public String getName() {
        return this.name;
    }

    public Technology name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TechCategory getCategory() {
        return this.category;
    }

    public Technology category(TechCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(TechCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public Technology description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getInheritsFrom() {
        return this.inheritsFrom;
    }

    public Technology inheritsFrom(UUID inheritsFrom) {
        this.setInheritsFrom(inheritsFrom);
        return this;
    }

    public void setInheritsFrom(UUID inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public TechStack getTechStackType() {
        return this.techStackType;
    }

    public Technology techStackType(TechStack techStackType) {
        this.setTechStackType(techStackType);
        return this;
    }

    public void setTechStackType(TechStack techStackType) {
        this.techStackType = techStackType;
    }

    public Technology getInheritsFrom() {
        return this.inheritsFrom;
    }

    public void setInheritsFrom(Technology technology) {
        this.inheritsFrom = technology;
    }

    public Technology inheritsFrom(Technology technology) {
        this.setInheritsFrom(technology);
        return this;
    }

    public Set<Scenario> getScenarios() {
        return this.scenarios;
    }

    public void setScenarios(Set<Scenario> scenarios) {
        if (this.scenarios != null) {
            this.scenarios.forEach(i -> i.setTechnologyID(null));
        }
        if (scenarios != null) {
            scenarios.forEach(i -> i.setTechnologyID(this));
        }
        this.scenarios = scenarios;
    }

    public Technology scenarios(Set<Scenario> scenarios) {
        this.setScenarios(scenarios);
        return this;
    }

    public Technology addScenario(Scenario scenario) {
        this.scenarios.add(scenario);
        scenario.setTechnologyID(this);
        return this;
    }

    public Technology removeScenario(Scenario scenario) {
        this.scenarios.remove(scenario);
        scenario.setTechnologyID(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Technology)) {
            return false;
        }
        return technologyID != null && technologyID.equals(((Technology) o).technologyID);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Technology{" +
            "technologyID=" + getTechnologyID() +
            ", name='" + getName() + "'" +
            ", category='" + getCategory() + "'" +
            ", description='" + getDescription() + "'" +
            ", inheritsFrom='" + getInheritsFrom() + "'" +
            ", techStackType='" + getTechStackType() + "'" +
            "}";
    }
}
