package com.gobr.pragrisk.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gobr.pragrisk.domain.enumeration.Environment;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Actor.
 */
@Entity
@Table(name = "actor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "actor")
public class Actor implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Column(name = "actor_id", nullable = false, unique = true)
    private UUID actorID;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "environ_ment", nullable = false)
    private Environment environMent;

    @Column(name = "inherits_from")
    private UUID inheritsFrom;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @JsonIgnoreProperties(value = { "inheritsFrom", "scenarios" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Actor inheritsFrom;

    @OneToMany(mappedBy = "actorID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "actorID", "technologyID", "vulnerabilityID" }, allowSetters = true)
    private Set<Scenario> scenarios = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getActorID() {
        return this.actorID;
    }

    public Actor actorID(UUID actorID) {
        this.setActorID(actorID);
        return this;
    }

    public void setActorID(UUID actorID) {
        this.actorID = actorID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Actor firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Actor lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public Actor nickName(String nickName) {
        this.setNickName(nickName);
        return this;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Environment getEnvironMent() {
        return this.environMent;
    }

    public Actor environMent(Environment environMent) {
        this.setEnvironMent(environMent);
        return this;
    }

    public void setEnvironMent(Environment environMent) {
        this.environMent = environMent;
    }

    public UUID getInheritsFrom() {
        return this.inheritsFrom;
    }

    public Actor inheritsFrom(UUID inheritsFrom) {
        this.setInheritsFrom(inheritsFrom);
        return this;
    }

    public void setInheritsFrom(UUID inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public String getDescription() {
        return this.description;
    }

    public Actor description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Actor getInheritsFrom() {
        return this.inheritsFrom;
    }

    public void setInheritsFrom(Actor actor) {
        this.inheritsFrom = actor;
    }

    public Actor inheritsFrom(Actor actor) {
        this.setInheritsFrom(actor);
        return this;
    }

    public Set<Scenario> getScenarios() {
        return this.scenarios;
    }

    public void setScenarios(Set<Scenario> scenarios) {
        if (this.scenarios != null) {
            this.scenarios.forEach(i -> i.setActorID(null));
        }
        if (scenarios != null) {
            scenarios.forEach(i -> i.setActorID(this));
        }
        this.scenarios = scenarios;
    }

    public Actor scenarios(Set<Scenario> scenarios) {
        this.setScenarios(scenarios);
        return this;
    }

    public Actor addScenario(Scenario scenario) {
        this.scenarios.add(scenario);
        scenario.setActorID(this);
        return this;
    }

    public Actor removeScenario(Scenario scenario) {
        this.scenarios.remove(scenario);
        scenario.setActorID(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actor)) {
            return false;
        }
        return actorID != null && actorID.equals(((Actor) o).actorID);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Actor{" +
            "actorID=" + getActorID() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", nickName='" + getNickName() + "'" +
            ", environMent='" + getEnvironMent() + "'" +
            ", inheritsFrom='" + getInheritsFrom() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
