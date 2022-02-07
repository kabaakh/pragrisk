package com.gobr.pragrisk.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Actor;
import com.gobr.pragrisk.repository.ActorRepository;
import com.gobr.pragrisk.repository.search.ActorSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Actor}.
 */
@Service
@Transactional
public class ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;

    private final ActorSearchRepository actorSearchRepository;

    public ActorService(ActorRepository actorRepository, ActorSearchRepository actorSearchRepository) {
        this.actorRepository = actorRepository;
        this.actorSearchRepository = actorSearchRepository;
    }

    /**
     * Save a actor.
     *
     * @param actor the entity to save.
     * @return the persisted entity.
     */
    public Actor save(Actor actor) {
        log.debug("Request to save Actor : {}", actor);
        Actor result = actorRepository.save(actor);
        actorSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a actor.
     *
     * @param actor the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Actor> partialUpdate(Actor actor) {
        log.debug("Request to partially update Actor : {}", actor);

        return actorRepository
            .findById(actor.getId())
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
                if (actor.getGroup() != null) {
                    existingActor.setGroup(actor.getGroup());
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
    }

    /**
     * Get all the actors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Actor> findAll(Pageable pageable) {
        log.debug("Request to get all Actors");
        return actorRepository.findAll(pageable);
    }

    /**
     * Get one actor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Actor> findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        return actorRepository.findById(id);
    }

    /**
     * Delete the actor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        actorRepository.deleteById(id);
        actorSearchRepository.deleteById(id);
    }

    /**
     * Search for the actor corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Actor> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Actors for query {}", query);
        return actorSearchRepository.search(query, pageable);
    }
}
