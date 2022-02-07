package com.gobr.pragrisk.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Scenario;
import com.gobr.pragrisk.repository.ScenarioRepository;
import com.gobr.pragrisk.repository.search.ScenarioSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Scenario}.
 */
@Service
@Transactional
public class ScenarioService {

    private final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    private final ScenarioRepository scenarioRepository;

    private final ScenarioSearchRepository scenarioSearchRepository;

    public ScenarioService(ScenarioRepository scenarioRepository, ScenarioSearchRepository scenarioSearchRepository) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioSearchRepository = scenarioSearchRepository;
    }

    /**
     * Save a scenario.
     *
     * @param scenario the entity to save.
     * @return the persisted entity.
     */
    public Scenario save(Scenario scenario) {
        log.debug("Request to save Scenario : {}", scenario);
        Scenario result = scenarioRepository.save(scenario);
        scenarioSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a scenario.
     *
     * @param scenario the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Scenario> partialUpdate(Scenario scenario) {
        log.debug("Request to partially update Scenario : {}", scenario);

        return scenarioRepository
            .findById(scenario.getId())
            .map(existingScenario -> {
                if (scenario.getTitle() != null) {
                    existingScenario.setTitle(scenario.getTitle());
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
    }

    /**
     * Get all the scenarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Scenario> findAll(Pageable pageable) {
        log.debug("Request to get all Scenarios");
        return scenarioRepository.findAll(pageable);
    }

    /**
     * Get one scenario by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Scenario> findOne(Long id) {
        log.debug("Request to get Scenario : {}", id);
        return scenarioRepository.findById(id);
    }

    /**
     * Delete the scenario by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Scenario : {}", id);
        scenarioRepository.deleteById(id);
        scenarioSearchRepository.deleteById(id);
    }

    /**
     * Search for the scenario corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Scenario> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Scenarios for query {}", query);
        return scenarioSearchRepository.search(query, pageable);
    }
}
