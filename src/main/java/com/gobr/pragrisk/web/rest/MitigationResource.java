package com.gobr.pragrisk.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.gobr.pragrisk.domain.Mitigation;
import com.gobr.pragrisk.repository.MitigationRepository;
import com.gobr.pragrisk.service.MitigationService;
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
 * REST controller for managing {@link com.gobr.pragrisk.domain.Mitigation}.
 */
@RestController
@RequestMapping("/api")
public class MitigationResource {

    private final Logger log = LoggerFactory.getLogger(MitigationResource.class);

    private static final String ENTITY_NAME = "mitigation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MitigationService mitigationService;

    private final MitigationRepository mitigationRepository;

    public MitigationResource(MitigationService mitigationService, MitigationRepository mitigationRepository) {
        this.mitigationService = mitigationService;
        this.mitigationRepository = mitigationRepository;
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
        if (mitigation.getMitigationID() != null) {
            throw new BadRequestAlertException("A new mitigation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Mitigation result = mitigationService.save(mitigation);
        return ResponseEntity
            .created(new URI("/api/mitigations/" + result.getMitigationID()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getMitigationID().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mitigations/:mitigationID} : Updates an existing mitigation.
     *
     * @param mitigationID the id of the mitigation to save.
     * @param mitigation the mitigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mitigation,
     * or with status {@code 400 (Bad Request)} if the mitigation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mitigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mitigations/{mitigationID}")
    public ResponseEntity<Mitigation> updateMitigation(
        @PathVariable(value = "mitigationID", required = false) final UUID mitigationID,
        @Valid @RequestBody Mitigation mitigation
    ) throws URISyntaxException {
        log.debug("REST request to update Mitigation : {}, {}", mitigationID, mitigation);
        if (mitigation.getMitigationID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(mitigationID, mitigation.getMitigationID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mitigationRepository.existsById(mitigationID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Mitigation result = mitigationService.save(mitigation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mitigation.getMitigationID().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /mitigations/:mitigationID} : Partial updates given fields of an existing mitigation, field will ignore if it is null
     *
     * @param mitigationID the id of the mitigation to save.
     * @param mitigation the mitigation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mitigation,
     * or with status {@code 400 (Bad Request)} if the mitigation is not valid,
     * or with status {@code 404 (Not Found)} if the mitigation is not found,
     * or with status {@code 500 (Internal Server Error)} if the mitigation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mitigations/{mitigationID}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mitigation> partialUpdateMitigation(
        @PathVariable(value = "mitigationID", required = false) final UUID mitigationID,
        @NotNull @RequestBody Mitigation mitigation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mitigation partially : {}, {}", mitigationID, mitigation);
        if (mitigation.getMitigationID() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(mitigationID, mitigation.getMitigationID())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mitigationRepository.existsById(mitigationID)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mitigation> result = mitigationService.partialUpdate(mitigation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mitigation.getMitigationID().toString())
        );
    }

    /**
     * {@code GET  /mitigations} : get all the mitigations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mitigations in body.
     */
    @GetMapping("/mitigations")
    public ResponseEntity<List<Mitigation>> getAllMitigations(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Mitigations");
        Page<Mitigation> page = mitigationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
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
        Optional<Mitigation> mitigation = mitigationService.findOne(id);
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
        mitigationService.delete(id);
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
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/mitigations")
    public ResponseEntity<List<Mitigation>> searchMitigations(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Mitigations for query {}", query);
        Page<Mitigation> page = mitigationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
