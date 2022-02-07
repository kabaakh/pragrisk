package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Mitigation;
import com.gobr.pragrisk.repository.MitigationRepository;
import com.gobr.pragrisk.repository.search.MitigationSearchRepository;
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
 * REST controller for managing {@link com.gobr.pragrisk.domain.Mitigation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MitigationResource {

    private final Logger log = LoggerFactory.getLogger(MitigationResource.class);

    private static final String ENTITY_NAME = "mitigation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MitigationRepository mitigationRepository;

    private final MitigationSearchRepository mitigationSearchRepository;

    public MitigationResource(MitigationRepository mitigationRepository, MitigationSearchRepository mitigationSearchRepository) {
        this.mitigationRepository = mitigationRepository;
        this.mitigationSearchRepository = mitigationSearchRepository;
    }

    /**
     * {@code POST  /mitigations} : Create a new mitigation.
     *
     * @param mitigation the mitigation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mitigation, or with status {@code 400 (Bad Request)} if the mitigation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mitigations")
    public ResponseEntity<Mitigation> createMitigation(@Valid @RequestBody Mitigation mitigation) throws URISyntaxException {
        log.debug("REST request to save Mitigation : {}", mitigation);
        if (mitigation.getVulnerabiltyID() != null) {
            throw new BadRequestAlertException("A new mitigation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Mitigation result = mitigationRepository.save(mitigation);
        mitigationSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/mitigations/" + result.getVulnerabiltyID()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getVulnerabiltyID().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mitigations/:vulnerabiltyID} : Updates an existing mitigation.
     *
     * @param vulnerabiltyID the id of the mitigation to save.
     * @param mitigation the mitigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mitigation,
     * or with status {@code 400 (Bad Request)} if the mitigation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mitigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mitigations/{vulnerabiltyID}")
    public ResponseEntity<Mitigation> updateMitigation(
        @PathVariable(value = "vulnerabiltyID", required = false) final UUID vulnerabiltyID,
        @Valid @RequestBody Mitigation mitigation
    ) throws URISyntaxException {
        log.debug("REST request to update Mitigation : {}, {}", vulnerabiltyID, mitigation);
        if (mitigation.getVulnerabiltyID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(vulnerabiltyID, mitigation.getVulnerabiltyID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mitigationRepository.existsById(vulnerabiltyID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Mitigation result = mitigationRepository.save(mitigation);
        mitigationSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mitigation.getVulnerabiltyID().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /mitigations/:vulnerabiltyID} : Partial updates given fields of an existing mitigation, field will ignore if it is null
     *
     * @param vulnerabiltyID the id of the mitigation to save.
     * @param mitigation the mitigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mitigation,
     * or with status {@code 400 (Bad Request)} if the mitigation is not valid,
     * or with status {@code 404 (Not Found)} if the mitigation is not found,
     * or with status {@code 500 (Internal Server Error)} if the mitigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mitigations/{vulnerabiltyID}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mitigation> partialUpdateMitigation(
        @PathVariable(value = "vulnerabiltyID", required = false) final UUID vulnerabiltyID,
        @NotNull @RequestBody Mitigation mitigation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mitigation partially : {}, {}", vulnerabiltyID, mitigation);
        if (mitigation.getVulnerabiltyID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(vulnerabiltyID, mitigation.getVulnerabiltyID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mitigationRepository.existsById(vulnerabiltyID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mitigation> result = mitigationRepository
            .findById(mitigation.getVulnerabiltyID())
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

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mitigation.getVulnerabiltyID().toString())
        );
    }

    /**
     * {@code GET  /mitigations} : get all the mitigations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mitigations in body.
     */
    @GetMapping("/mitigations")
    public List<Mitigation> getAllMitigations() {
        log.debug("REST request to get all Mitigations");
        return mitigationRepository.findAll();
    }

    /**
     * {@code GET  /mitigations/:id} : get the "id" mitigation.
     *
     * @param id the id of the mitigation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mitigation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mitigations/{id}")
    public ResponseEntity<Mitigation> getMitigation(@PathVariable UUID id) {
        log.debug("REST request to get Mitigation : {}", id);
        Optional<Mitigation> mitigation = mitigationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mitigation);
    }

    /**
     * {@code DELETE  /mitigations/:id} : delete the "id" mitigation.
     *
     * @param id the id of the mitigation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mitigations/{id}")
    public ResponseEntity<Void> deleteMitigation(@PathVariable UUID id) {
        log.debug("REST request to delete Mitigation : {}", id);
        mitigationRepository.deleteById(id);
        mitigationSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/mitigations?query=:query} : search for the mitigation corresponding
     * to the query.
     *
     * @param query the query of the mitigation search.
     * @return the result of the search.
     */
    @GetMapping("/_search/mitigations")
    public List<Mitigation> searchMitigations(@RequestParam String query) {
        log.debug("REST request to search Mitigations for query {}", query);
        return StreamSupport.stream(mitigationSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
