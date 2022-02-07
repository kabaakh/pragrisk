package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Scenario;
import com.gobr.pragrisk.repository.ScenarioRepository;
import com.gobr.pragrisk.service.ScenarioService;
import com.gobr.pragrisk.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gobr.pragrisk.domain.Scenario}.
 */
@RestController
@RequestMapping("/api")
public class ScenarioResource {

    private final Logger log = LoggerFactory.getLogger(ScenarioResource.class);

    private static final String ENTITY_NAME = "scenario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScenarioService scenarioService;

    private final ScenarioRepository scenarioRepository;

    public ScenarioResource(ScenarioService scenarioService, ScenarioRepository scenarioRepository) {
        this.scenarioService = scenarioService;
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * {@code POST  /scenarios} : Create a new scenario.
     *
     * @param scenario the scenario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scenario, or with status {@code 400 (Bad Request)} if the scenario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/scenarios")
    public ResponseEntity<Scenario> createScenario(@Valid @RequestBody Scenario scenario) throws URISyntaxException {
        log.debug("REST request to save Scenario : {}", scenario);
        if (scenario.getScenarioID() != null) {
            throw new BadRequestAlertException("A new scenario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Scenario result = scenarioService.save(scenario);
        return ResponseEntity
            .created(new URI("/api/scenarios/" + result.getScenarioID()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getScenarioID().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /scenarios/:scenarioID} : Updates an existing scenario.
     *
     * @param scenarioID the id of the scenario to save.
     * @param scenario the scenario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenario,
     * or with status {@code 400 (Bad Request)} if the scenario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scenario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/scenarios/{scenarioID}")
    public ResponseEntity<Scenario> updateScenario(
        @PathVariable(value = "scenarioID", required = false) final UUID scenarioID,
        @Valid @RequestBody Scenario scenario
    ) throws URISyntaxException {
        log.debug("REST request to update Scenario : {}, {}", scenarioID, scenario);
        if (scenario.getScenarioID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(scenarioID, scenario.getScenarioID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(scenarioID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Scenario result = scenarioService.save(scenario);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scenario.getScenarioID().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /scenarios/:scenarioID} : Partial updates given fields of an existing scenario, field will ignore if it is null
     *
     * @param scenarioID the id of the scenario to save.
     * @param scenario the scenario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scenario,
     * or with status {@code 400 (Bad Request)} if the scenario is not valid,
     * or with status {@code 404 (Not Found)} if the scenario is not found,
     * or with status {@code 500 (Internal Server Error)} if the scenario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/scenarios/{scenarioID}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Scenario> partialUpdateScenario(
        @PathVariable(value = "scenarioID", required = false) final UUID scenarioID,
        @NotNull @RequestBody Scenario scenario
    ) throws URISyntaxException {
        log.debug("REST request to partial update Scenario partially : {}, {}", scenarioID, scenario);
        if (scenario.getScenarioID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(scenarioID, scenario.getScenarioID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scenarioRepository.existsById(scenarioID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Scenario> result = scenarioService.partialUpdate(scenario);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scenario.getScenarioID().toString())
        );
    }

    /**
     * {@code GET  /scenarios} : get all the scenarios.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<Scenario>> getAllScenarios(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Scenarios");
        Page<Scenario> page = scenarioService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scenarios/:id} : get the "id" scenario.
     *
     * @param id the id of the scenario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scenario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scenarios/{id}")
    public ResponseEntity<Scenario> getScenario(@PathVariable UUID id) {
        log.debug("REST request to get Scenario : {}", id);
        Optional<Scenario> scenario = scenarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scenario);
    }

    /**
     * {@code DELETE  /scenarios/:id} : delete the "id" scenario.
     *
     * @param id the id of the scenario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/scenarios/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable UUID id) {
        log.debug("REST request to delete Scenario : {}", id);
        scenarioService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/scenarios?query=:query} : search for the scenario corresponding
     * to the query.
     *
     * @param query the query of the scenario search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/scenarios")
    public ResponseEntity<List<Scenario>> searchScenarios(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Scenarios for query {}", query);
        Page<Scenario> page = scenarioService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
