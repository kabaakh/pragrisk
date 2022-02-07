package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Environment;
import com.gobr.pragrisk.repository.EnvironmentRepository;
import com.gobr.pragrisk.service.EnvironmentService;
import com.gobr.pragrisk.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gobr.pragrisk.domain.Environment}.
 */
@RestController
@RequestMapping("/api")
public class EnvironmentResource {

    private final Logger log = LoggerFactory.getLogger(EnvironmentResource.class);

    private static final String ENTITY_NAME = "environment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvironmentService environmentService;

    private final EnvironmentRepository environmentRepository;

    public EnvironmentResource(EnvironmentService environmentService, EnvironmentRepository environmentRepository) {
        this.environmentService = environmentService;
        this.environmentRepository = environmentRepository;
    }

    /**
     * {@code POST  /environments} : Create a new environment.
     *
     * @param environment the environment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new environment, or with status {@code 400 (Bad Request)} if the environment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/environments")
    public ResponseEntity<Environment> createEnvironment(@Valid @RequestBody Environment environment) throws URISyntaxException {
        log.debug("REST request to save Environment : {}", environment);
        if (environment.getId() != null) {
            throw new BadRequestAlertException("A new environment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Environment result = environmentService.save(environment);
        return ResponseEntity
            .created(new URI("/api/environments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /environments/:id} : Updates an existing environment.
     *
     * @param id the id of the environment to save.
     * @param environment the environment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environment,
     * or with status {@code 400 (Bad Request)} if the environment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/environments/{id}")
    public ResponseEntity<Environment> updateEnvironment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Environment environment
    ) throws URISyntaxException {
        log.debug("REST request to update Environment : {}, {}", id, environment);
        if (environment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Environment result = environmentService.save(environment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /environments/:id} : Partial updates given fields of an existing environment, field will ignore if it is null
     *
     * @param id the id of the environment to save.
     * @param environment the environment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environment,
     * or with status {@code 400 (Bad Request)} if the environment is not valid,
     * or with status {@code 404 (Not Found)} if the environment is not found,
     * or with status {@code 500 (Internal Server Error)} if the environment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/environments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Environment> partialUpdateEnvironment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Environment environment
    ) throws URISyntaxException {
        log.debug("REST request to partial update Environment partially : {}, {}", id, environment);
        if (environment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Environment> result = environmentService.partialUpdate(environment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, environment.getId().toString())
        );
    }

    /**
     * {@code GET  /environments} : get all the environments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of environments in body.
     */
    @GetMapping("/environments")
    public List<Environment> getAllEnvironments() {
        log.debug("REST request to get all Environments");
        return environmentService.findAll();
    }

    /**
     * {@code GET  /environments/:id} : get the "id" environment.
     *
     * @param id the id of the environment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the environment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/environments/{id}")
    public ResponseEntity<Environment> getEnvironment(@PathVariable Long id) {
        log.debug("REST request to get Environment : {}", id);
        Optional<Environment> environment = environmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(environment);
    }

    /**
     * {@code DELETE  /environments/:id} : delete the "id" environment.
     *
     * @param id the id of the environment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/environments/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        log.debug("REST request to delete Environment : {}", id);
        environmentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/environments?query=:query} : search for the environment corresponding
     * to the query.
     *
     * @param query the query of the environment search.
     * @return the result of the search.
     */
    @GetMapping("/_search/environments")
    public List<Environment> searchEnvironments(@RequestParam String query) {
        log.debug("REST request to search Environments for query {}", query);
        return environmentService.search(query);
    }
}
