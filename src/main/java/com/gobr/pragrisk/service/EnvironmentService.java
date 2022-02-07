package com.gobr.pragrisk.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Environment;
import com.gobr.pragrisk.repository.EnvironmentRepository;
import com.gobr.pragrisk.repository.search.EnvironmentSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Environment}.
 */
@Service
@Transactional
public class EnvironmentService {

    private final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

    private final EnvironmentRepository environmentRepository;

    private final EnvironmentSearchRepository environmentSearchRepository;

    public EnvironmentService(EnvironmentRepository environmentRepository, EnvironmentSearchRepository environmentSearchRepository) {
        this.environmentRepository = environmentRepository;
        this.environmentSearchRepository = environmentSearchRepository;
    }

    /**
     * Save a environment.
     *
     * @param environment the entity to save.
     * @return the persisted entity.
     */
    public Environment save(Environment environment) {
        log.debug("Request to save Environment : {}", environment);
        Environment result = environmentRepository.save(environment);
        environmentSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a environment.
     *
     * @param environment the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Environment> partialUpdate(Environment environment) {
        log.debug("Request to partially update Environment : {}", environment);

        return environmentRepository
            .findById(environment.getId())
            .map(existingEnvironment -> {
                if (environment.getName() != null) {
                    existingEnvironment.setName(environment.getName());
                }
                if (environment.getDescription() != null) {
                    existingEnvironment.setDescription(environment.getDescription());
                }

                return existingEnvironment;
            })
            .map(environmentRepository::save)
            .map(savedEnvironment -> {
                environmentSearchRepository.save(savedEnvironment);

                return savedEnvironment;
            });
    }

    /**
     * Get all the environments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Environment> findAll() {
        log.debug("Request to get all Environments");
        return environmentRepository.findAll();
    }

    /**
     * Get one environment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Environment> findOne(Long id) {
        log.debug("Request to get Environment : {}", id);
        return environmentRepository.findById(id);
    }

    /**
     * Delete the environment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Environment : {}", id);
        environmentRepository.deleteById(id);
        environmentSearchRepository.deleteById(id);
    }

    /**
     * Search for the environment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Environment> search(String query) {
        log.debug("Request to search Environments for query {}", query);
        return StreamSupport.stream(environmentSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
