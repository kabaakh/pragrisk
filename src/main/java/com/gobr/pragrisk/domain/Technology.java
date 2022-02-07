package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobr.pragrisk.domain.enumeration.TechCategory;
import com.gobr.pragrisk.domain.enumeration.TechStack;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tech_stack_type", nullable = false)
    private TechStack techStackType;

    @JsonIgnoreProperties(value = { "parentTechnology", "technologyIDS" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Technology parentTechnology;

    @OneToMany(mappedBy = "technologyFK")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "actorFK", "technologyFK", "vulnerabilityFK" }, allowSetters = true)
    private Set<Scenario> technologyIDS = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Technology id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Technology getParentTechnology() {
        return this.parentTechnology;
    }

    public void setParentTechnology(Technology technology) {
        this.parentTechnology = technology;
    }

    public Technology parentTechnology(Technology technology) {
        this.setParentTechnology(technology);
        return this;
    }

    public Set<Scenario> getTechnologyIDS() {
        return this.technologyIDS;
    }

    public void setTechnologyIDS(Set<Scenario> scenarios) {
        if (this.technologyIDS != null) {
            this.technologyIDS.forEach(i -> i.setTechnologyFK(null));
        }
        if (scenarios != null) {
            scenarios.forEach(i -> i.setTechnologyFK(this));
        }
        this.technologyIDS = scenarios;
    }

    public Technology technologyIDS(Set<Scenario> scenarios) {
        this.setTechnologyIDS(scenarios);
        return this;
    }

    public Technology addTechnologyID(Scenario scenario) {
        this.technologyIDS.add(scenario);
        scenario.setTechnologyFK(this);
        return this;
    }

    public Technology removeTechnologyID(Scenario scenario) {
        this.technologyIDS.remove(scenario);
        scenario.setTechnologyFK(null);
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
        return id != null && id.equals(((Technology) o).id);
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
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", category='" + getCategory() + "'" +
            ", description='" + getDescription() + "'" +
            ", techStackType='" + getTechStackType() + "'" +
            "}";
    }
}
