package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Scenario;
import com.gobr.pragrisk.repository.ScenarioRepository;
import com.gobr.pragrisk.repository.search.ScenarioSearchRepository;
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
 * REST controller for managing {@link com.gobr.pragrisk.domain.Scenario}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ScenarioResource {

    private final Logger log = LoggerFactory.getLogger(ScenarioResource.class);

    private static final String ENTITY_NAME = "scenario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScenarioRepository scenarioRepository;

    private final ScenarioSearchRepository scenarioSearchRepository;

    public ScenarioResource(ScenarioRepository scenarioRepository, ScenarioSearchRepository scenarioSearchRepository) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioSearchRepository = scenarioSearchRepository;
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
        Scenario result = scenarioRepository.save(scenario);
        scenarioSearchRepository.save(result);
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

        Scenario result = scenarioRepository.save(scenario);
        scenarioSearchRepository.save(result);
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

        Optional<Scenario> result = scenarioRepository
            .findById(scenario.getScenarioID())
            .map(existingScenario -> {
                if (scenario.getActorFK() != null) {
                    existingScenario.setActorFK(scenario.getActorFK());
                }
                if (scenario.getTechnologyFK() != null) {
                    existingScenario.setTechnologyFK(scenario.getTechnologyFK());
                }
                if (scenario.getVulnerabilityFK() != null) {
                    existingScenario.setVulnerabilityFK(scenario.getVulnerabilityFK());
                }
                if (scenario.getDescription() != null) {
                    existingScenario.setDescription(scenario.getDescription());
                }
                if (scenario.getProbability() != null) {
                    existingScenario.setProbability(scenario.getProbability());
                }
                if (scenario.getQonsequence() != null) {
                    existingScenario.setQonsequence(scenario.getQonsequence());
                }
                if (scenario.getRiskValue() != null) {
                    existingScenario.setRiskValue(scenario.getRiskValue());
                }

                return existingScenario;
            })
            .map(scenarioRepository::save)
            .map(savedScenario -> {
                scenarioSearchRepository.save(savedScenario);

                return savedScenario;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scenario.getScenarioID().toString())
        );
    }

    /**
     * {@code GET  /scenarios} : get all the scenarios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scenarios in body.
     */
    @GetMapping("/scenarios")
    public List<Scenario> getAllScenarios() {
        log.debug("REST request to get all Scenarios");
        return scenarioRepository.findAll();
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
        Optional<Scenario> scenario = scenarioRepository.findById(id);
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
        scenarioRepository.deleteById(id);
        scenarioSearchRepository.deleteById(id);
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
     * @return the result of the search.
     */
    @GetMapping("/_search/scenarios")
    public List<Scenario> searchScenarios(@RequestParam String query) {
        log.debug("REST request to search Scenarios for query {}", query);
        return StreamSupport.stream(scenarioSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
