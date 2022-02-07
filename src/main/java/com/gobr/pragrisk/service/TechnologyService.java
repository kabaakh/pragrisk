package com.gobr.pragrisk.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Technology;
import com.gobr.pragrisk.repository.TechnologyRepository;
import com.gobr.pragrisk.repository.search.TechnologySearchRepository;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Technology}.
 */
@Service
@Transactional
public class TechnologyService {

    private final Logger log = LoggerFactory.getLogger(TechnologyService.class);

    private final TechnologyRepository technologyRepository;

    private final TechnologySearchRepository technologySearchRepository;

    public TechnologyService(TechnologyRepository technologyRepository, TechnologySearchRepository technologySearchRepository) {
        this.technologyRepository = technologyRepository;
        this.technologySearchRepository = technologySearchRepository;
    }

    /**
     * Save a technology.
     *
     * @param technology the entity to save.
     * @return the persisted entity.
     */
    public Technology save(Technology technology) {
        log.debug("Request to save Technology : {}", technology);
        Technology result = technologyRepository.save(technology);
        technologySearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a technology.
     *
     * @param technology the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Technology> partialUpdate(Technology technology) {
        log.debug("Request to partially update Technology : {}", technology);

        return technologyRepository
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
    }

    /**
     * Get all the technologies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Technology> findAll(Pageable pageable) {
        log.debug("Request to get all Technologies");
        return technologyRepository.findAll(pageable);
    }

    /**
     * Get one technology by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Technology> findOne(UUID id) {
        log.debug("Request to get Technology : {}", id);
        return technologyRepository.findById(id);
    }

    /**
     * Delete the technology by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Technology : {}", id);
        technologyRepository.deleteById(id);
        technologySearchRepository.deleteById(id);
    }

    /**
     * Search for the technology corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Technology> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Technologies for query {}", query);
        return technologySearchRepository.search(query, pageable);
    }
}
