package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Technology;
import com.gobr.pragrisk.repository.TechnologyRepository;
import com.gobr.pragrisk.repository.search.TechnologySearchRepository;
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
 * REST controller for managing {@link com.gobr.pragrisk.domain.Technology}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TechnologyResource {

    private final Logger log = LoggerFactory.getLogger(TechnologyResource.class);

    private static final String ENTITY_NAME = "technology";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TechnologyRepository technologyRepository;

    private final TechnologySearchRepository technologySearchRepository;

    public TechnologyResource(TechnologyRepository technologyRepository, TechnologySearchRepository technologySearchRepository) {
        this.technologyRepository = technologyRepository;
        this.technologySearchRepository = technologySearchRepository;
    }

    /**
     * {@code POST  /technologies} : Create a new technology.
     *
     * @param technology the technology to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new technology, or with status {@code 400 (Bad Request)} if the technology has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/technologies")
    public ResponseEntity<Technology> createTechnology(@Valid @RequestBody Technology technology) throws URISyntaxException {
        log.debug("REST request to save Technology : {}", technology);
        if (technology.getTechnologyID() != null) {
            throw new BadRequestAlertException("A new technology cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Technology result = technologyRepository.save(technology);
        technologySearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/technologies/" + result.getTechnologyID()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getTechnologyID().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /technologies/:technologyID} : Updates an existing technology.
     *
     * @param technologyID the id of the technology to save.
     * @param technology the technology to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technology,
     * or with status {@code 400 (Bad Request)} if the technology is not valid,
     * or with status {@code 500 (Internal Server Error)} if the technology couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/technologies/{technologyID}")
    public ResponseEntity<Technology> updateTechnology(
        @PathVariable(value = "technologyID", required = false) final UUID technologyID,
        @Valid @RequestBody Technology technology
    ) throws URISyntaxException {
        log.debug("REST request to update Technology : {}, {}", technologyID, technology);
        if (technology.getTechnologyID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(technologyID, technology.getTechnologyID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!technologyRepository.existsById(technologyID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Technology result = technologyRepository.save(technology);
        technologySearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, technology.getTechnologyID().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /technologies/:technologyID} : Partial updates given fields of an existing technology, field will ignore if it is null
     *
     * @param technologyID the id of the technology to save.
     * @param technology the technology to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technology,
     * or with status {@code 400 (Bad Request)} if the technology is not valid,
     * or with status {@code 404 (Not Found)} if the technology is not found,
     * or with status {@code 500 (Internal Server Error)} if the technology couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/technologies/{technologyID}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Technology> partialUpdateTechnology(
        @PathVariable(value = "technologyID", required = false) final UUID technologyID,
        @NotNull @RequestBody Technology technology
    ) throws URISyntaxException {
        log.debug("REST request to partial update Technology partially : {}, {}", technologyID, technology);
        if (technology.getTechnologyID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(technologyID, technology.getTechnologyID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!technologyRepository.existsById(technologyID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Technology> result = technologyRepository
            .findById(technology.getTechnologyID())
            .map(existingTechnology -> {
                if (technology.getName() != null) {
                    existingTechnology.setName(technology.getName());
                }
                if (technology.getCategory() != null) {
                    existingTechnology.setCategory(technology.getCategory());
                }
                if (technology.getDescription() != null) {
                    existingTechnology.setDescription(technology.getDescription());
                }
                if (technology.getInheritsFrom() != null) {
                    existingTechnology.setInheritsFrom(technology.getInheritsFrom());
                }
                if (technology.getTechStackType() != null) {
                    existingTechnology.setTechStackType(technology.getTechStackType());
                }

                return existingTechnology;
            })
            .map(technologyRepository::save)
            .map(savedTechnology -> {
                technologySearchRepository.save(savedTechnology);

                return savedTechnology;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, technology.getTechnologyID().toString())
        );
    }

    /**
     * {@code GET  /technologies} : get all the technologies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of technologies in body.
     */
    @GetMapping("/technologies")
    public List<Technology> getAllTechnologies() {
        log.debug("REST request to get all Technologies");
        return technologyRepository.findAll();
    }

    /**
     * {@code GET  /technologies/:id} : get the "id" technology.
     *
     * @param id the id of the technology to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the technology, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/technologies/{id}")
    public ResponseEntity<Technology> getTechnology(@PathVariable UUID id) {
        log.debug("REST request to get Technology : {}", id);
        Optional<Technology> technology = technologyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(technology);
    }

    /**
     * {@code DELETE  /technologies/:id} : delete the "id" technology.
     *
     * @param id the id of the technology to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/technologies/{id}")
    public ResponseEntity<Void> deleteTechnology(@PathVariable UUID id) {
        log.debug("REST request to delete Technology : {}", id);
        technologyRepository.deleteById(id);
        technologySearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/technologies?query=:query} : search for the technology corresponding
     * to the query.
     *
     * @param query the query of the technology search.
     * @return the result of the search.
     */
    @GetMapping("/_search/technologies")
    public List<Technology> searchTechnologies(@RequestParam String query) {
        log.debug("REST request to search Technologies for query {}", query);
        return StreamSupport.stream(technologySearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}