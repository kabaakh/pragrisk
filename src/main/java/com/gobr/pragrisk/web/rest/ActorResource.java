package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Actor;
import com.gobr.pragrisk.repository.ActorRepository;
import com.gobr.pragrisk.repository.search.ActorSearchRepository;
import com.gobr.pragrisk.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gobr.pragrisk.domain.Actor}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ActorResource {

    private final Logger log = LoggerFactory.getLogger(ActorResource.class);

    private static final String ENTITY_NAME = "actor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActorRepository actorRepository;

    private final ActorSearchRepository actorSearchRepository;

    public ActorResource(ActorRepository actorRepository, ActorSearchRepository actorSearchRepository) {
        this.actorRepository = actorRepository;
        this.actorSearchRepository = actorSearchRepository;
    }

    /**
     * {@code POST  /actors} : Create a new actor.
     *
     * @param actor the actor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actor, or with status {@code 400 (Bad Request)} if the actor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actors")
    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) throws URISyntaxException {
        log.debug("REST request to save Actor : {}", actor);
        if (actor.getActorID() != null) {
            throw new BadRequestAlertException("A new actor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Actor result = actorRepository.save(actor);
        actorSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/actors/" + result.getActorID()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getActorID().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /actors/:actorID} : Updates an existing actor.
     *
     * @param actorID the id of the actor to save.
     * @param actor the actor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actor,
     * or with status {@code 400 (Bad Request)} if the actor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actors/{actorID}")
    public ResponseEntity<Actor> updateActor(
        @PathVariable(value = "actorID", required = false) final UUID actorID,
        @Valid @RequestBody Actor actor
    ) throws URISyntaxException {
        log.debug("REST request to update Actor : {}, {}", actorID, actor);
        if (actor.getActorID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(actorID, actor.getActorID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actorRepository.existsById(actorID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Actor result = actorRepository.save(actor);
        actorSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actor.getActorID().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /actors/:actorID} : Partial updates given fields of an existing actor, field will ignore if it is null
     *
     * @param actorID the id of the actor to save.
     * @param actor the actor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actor,
     * or with status {@code 400 (Bad Request)} if the actor is not valid,
     * or with status {@code 404 (Not Found)} if the actor is not found,
     * or with status {@code 500 (Internal Server Error)} if the actor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/actors/{actorID}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Actor> partialUpdateActor(
        @PathVariable(value = "actorID", required = false) final UUID actorID,
        @NotNull @RequestBody Actor actor
    ) throws URISyntaxException {
        log.debug("REST request to partial update Actor partially : {}, {}", actorID, actor);
        if (actor.getActorID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(actorID, actor.getActorID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!actorRepository.existsById(actorID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Actor> result = actorRepository
            .findById(actor.getActorID())
            .map(existingActor -> {
                if (actor.getFirstName() != null) {
                    existingActor.setFirstName(actor.getFirstName());
                }
                if (actor.getLastName() != null) {
                    existingActor.setLastName(actor.getLastName());
                }
                if (actor.getNickName() != null) {
                    existingActor.setNickName(actor.getNickName());
                }
                if (actor.getEnvironMent() != null) {
                    existingActor.setEnvironMent(actor.getEnvironMent());
                }
                if (actor.getParentActor() != null) {
                    existingActor.setParentActor(actor.getParentActor());
                }
                if (actor.getDescription() != null) {
                    existingActor.setDescription(actor.getDescription());
                }

                return existingActor;
            })
            .map(actorRepository::save)
            .map(savedActor -> {
                actorSearchRepository.save(savedActor);

                return savedActor;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, actor.getActorID().toString())
        );
    }

    /**
     * {@code GET  /actors} : get all the actors.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actors in body.
     */
    @GetMapping("/actors")
    public List<Actor> getAllActors() {
        log.debug("REST request to get all Actors");
        return actorRepository.findAll();
    }

    /**
     * {@code GET  /actors/:id} : get the "id" actor.
     *
     * @param id the id of the actor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actors/{id}")
    public ResponseEntity<Actor> getActor(@PathVariable UUID id) {
        log.debug("REST request to get Actor : {}", id);
        Optional<Actor> actor = actorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(actor);
    }

    /**
     * {@code DELETE  /actors/:id} : delete the "id" actor.
     *
     * @param id the id of the actor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/actors/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable UUID id) {
        log.debug("REST request to delete Actor : {}", id);
        actorRepository.deleteById(id);
        actorSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/actors?query=:query} : search for the actor corresponding
     * to the query.
     *
     * @param query the query of the actor search.
     * @return the result of the search.
     */
    @GetMapping("/_search/actors")
    public List<Actor> searchActors(@RequestParam String query) {
        log.debug("REST request to search Actors for query {}", query);
        return StreamSupport.stream(actorSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
