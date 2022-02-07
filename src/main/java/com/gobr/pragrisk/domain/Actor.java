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
    @Column(name = "jhi_group", nullable = false)
    private Environment group;

    @Size(max = 1024)
    @Column(name = "description", length = 1024)
    private String description;

    @JsonIgnoreProperties(value = { "parentActor", "actorIDS" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Actor parentActor;

    @OneToMany(mappedBy = "actorFK")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "actorFK", "technologyFK", "vulnerabilityFK" }, allowSetters = true)
    private Set<Scenario> actorIDS = new HashSet<>();

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

    public Environment getGroup() {
        return this.group;
    }

    public Actor group(Environment group) {
        this.setGroup(group);
        return this;
    }

    public void setGroup(Environment group) {
        this.group = group;
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

    public Actor getParentActor() {
        return this.parentActor;
    }

    public void setParentActor(Actor actor) {
        this.parentActor = actor;
    }

    public Actor parentActor(Actor actor) {
        this.setParentActor(actor);
        return this;
    }

    public Set<Scenario> getActorIDS() {
        return this.actorIDS;
    }

    public void setActorIDS(Set<Scenario> scenarios) {
        if (this.actorIDS != null) {
            this.actorIDS.forEach(i -> i.setActorFK(null));
        }
        if (scenarios != null) {
            scenarios.forEach(i -> i.setActorFK(this));
        }
        this.actorIDS = scenarios;
    }

    public Actor actorIDS(Set<Scenario> scenarios) {
        this.setActorIDS(scenarios);
        return this;
    }

    public Actor addActorID(Scenario scenario) {
        this.actorIDS.add(scenario);
        scenario.setActorFK(this);
        return this;
    }

    public Actor removeActorID(Scenario scenario) {
        this.actorIDS.remove(scenario);
        scenario.setActorFK(null);
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
            ", group='" + getGroup() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
