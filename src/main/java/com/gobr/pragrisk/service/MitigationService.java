package com.gobr.pragrisk.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Mitigation;
import com.gobr.pragrisk.repository.MitigationRepository;
import com.gobr.pragrisk.repository.search.MitigationSearchRepository;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Mitigation}.
 */
@Service
@Transactional
public class MitigationService {

    private final Logger log = LoggerFactory.getLogger(MitigationService.class);

    private final MitigationRepository mitigationRepository;

    private final MitigationSearchRepository mitigationSearchRepository;

    public MitigationService(MitigationRepository mitigationRepository, MitigationSearchRepository mitigationSearchRepository) {
        this.mitigationRepository = mitigationRepository;
        this.mitigationSearchRepository = mitigationSearchRepository;
    }

    /**
     * Save a mitigation.
     *
     * @param mitigation the entity to save.
     * @return the persisted entity.
     */
    public Mitigation save(Mitigation mitigation) {
        log.debug("Request to save Mitigation : {}", mitigation);
        Mitigation result = mitigationRepository.save(mitigation);
        mitigationSearchRepository.save(result);
        return result;
    }

    /**
     * Partially update a mitigation.
     *
     * @param mitigation the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Mitigation> partialUpdate(Mitigation mitigation) {
        log.debug("Request to partially update Mitigation : {}", mitigation);

        return mitigationRepository
            .findById(mitigation.getMitigationID())
            .map(existingMitigation -> {
                if (mitigation.getControlID() != null) {
                    existingMitigation.setControlID(mitigation.getControlID());
                }
                if (mitigation.getReference() != null) {
                    existingMitigation.setReference(mitigation.getReference());
                }
                if (mitigation.getType() != null) {
                    existingMitigation.setType(mitigation.getType());
                }
                if (mitigation.getStatus() != null) {
                    existingMitigation.setStatus(mitigation.getStatus());
                }

                return existingMitigation;
            })
            .map(mitigationRepository::save)
            .map(savedMitigation -> {
                mitigationSearchRepository.save(savedMitigation);

                return savedMitigation;
            });
    }

    /**
     * Get all the mitigations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Mitigation> findAll(Pageable pageable) {
        log.debug("Request to get all Mitigations");
        return mitigationRepository.findAll(pageable);
    }

    /**
     * Get one mitigation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Mitigation> findOne(UUID id) {
        log.debug("Request to get Mitigation : {}", id);
        return mitigationRepository.findById(id);
    }

    /**
     * Delete the mitigation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        log.debug("Request to delete Mitigation : {}", id);
        mitigationRepository.deleteById(id);
        mitigationSearchRepository.deleteById(id);
    }

    /**
     * Search for the mitigation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Mitigation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Mitigations for query {}", query);
        return mitigationSearchRepository.search(query, pageable);
    }
}
