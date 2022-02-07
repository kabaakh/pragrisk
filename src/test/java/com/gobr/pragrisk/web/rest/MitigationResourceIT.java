package com.gobr.pragrisk.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gobr.pragrisk.IntegrationTest;
import com.gobr.pragrisk.domain.Mitigation;
import com.gobr.pragrisk.domain.enumeration.MitigationStatus;
import com.gobr.pragrisk.domain.enumeration.MitigationType;
import com.gobr.pragrisk.repository.MitigationRepository;
import com.gobr.pragrisk.repository.search.MitigationSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MitigationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MitigationResourceIT {

    private static final String DEFAULT_CONTROL_ID = "AAAAAAAAAA";
    private static final String UPDATED_CONTROL_ID = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final MitigationType DEFAULT_TYPE = MitigationType.PREV;
    private static final MitigationType UPDATED_TYPE = MitigationType.DETECT;

    private static final MitigationStatus DEFAULT_STATUS = MitigationStatus.MISS;
    private static final MitigationStatus UPDATED_STATUS = MitigationStatus.ADHOC;

    private static final String ENTITY_API_URL = "/api/mitigations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{mitigationID}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mitigations";

    @Autowired
    private MitigationRepository mitigationRepository;

    /**
     * This repository is mocked in the com.gobr.pragrisk.repository.search test package.
     *
     * @see com.gobr.pragrisk.repository.search.MitigationSearchRepositoryMockConfiguration
     */
    @Autowired
    private MitigationSearchRepository mockMitigationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMitigationMockMvc;

    private Mitigation mitigation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mitigation createEntity(EntityManager em) {
        Mitigation mitigation = new Mitigation()
            .controlID(DEFAULT_CONTROL_ID)
            .reference(DEFAULT_REFERENCE)
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS);
        return mitigation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mitigation createUpdatedEntity(EntityManager em) {
        Mitigation mitigation = new Mitigation()
            .controlID(UPDATED_CONTROL_ID)
            .reference(UPDATED_REFERENCE)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS);
        return mitigation;
    }

    @BeforeEach
    public void initTest() {
        mitigation = createEntity(em);
    }

    @Test
    @Transactional
    void createMitigation() throws Exception {
        int databaseSizeBeforeCreate = mitigationRepository.findAll().size();
        // Create the Mitigation
        restMitigationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isCreated());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeCreate + 1);
        Mitigation testMitigation = mitigationList.get(mitigationList.size() - 1);
        assertThat(testMitigation.getControlID()).isEqualTo(DEFAULT_CONTROL_ID);
        assertThat(testMitigation.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testMitigation.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testMitigation.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(1)).save(testMitigation);
    }

    @Test
    @Transactional
    void createMitigationWithExistingId() throws Exception {
        // Create the Mitigation with an existing ID
        mitigationRepository.saveAndFlush(mitigation);

        int databaseSizeBeforeCreate = mitigationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMitigationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isBadRequest());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void checkControlIDIsRequired() throws Exception {
        int databaseSizeBeforeTest = mitigationRepository.findAll().size();
        // set the field null
        mitigation.setControlID(null);

        // Create the Mitigation, which fails.

        restMitigationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isBadRequest());

        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = mitigationRepository.findAll().size();
        // set the field null
        mitigation.setType(null);

        // Create the Mitigation, which fails.

        restMitigationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isBadRequest());

        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = mitigationRepository.findAll().size();
        // set the field null
        mitigation.setStatus(null);

        // Create the Mitigation, which fails.

        restMitigationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isBadRequest());

        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMitigations() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        // Get all the mitigationList
        restMitigationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=mitigationID,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].mitigationID").value(hasItem(mitigation.getMitigationID().toString())))
            .andExpect(jsonPath("$.[*].controlID").value(hasItem(DEFAULT_CONTROL_ID)))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getMitigation() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        // Get the mitigation
        restMitigationMockMvc
            .perform(get(ENTITY_API_URL_ID, mitigation.getMitigationID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.mitigationID").value(mitigation.getMitigationID().toString()))
            .andExpect(jsonPath("$.controlID").value(DEFAULT_CONTROL_ID))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMitigation() throws Exception {
        // Get the mitigation
        restMitigationMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMitigation() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();

        // Update the mitigation
        Mitigation updatedMitigation = mitigationRepository.findById(mitigation.getMitigationID()).get();
        // Disconnect from session so that the updates on updatedMitigation are not directly saved in db
        em.detach(updatedMitigation);
        updatedMitigation.controlID(UPDATED_CONTROL_ID).reference(UPDATED_REFERENCE).type(UPDATED_TYPE).status(UPDATED_STATUS);

        restMitigationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMitigation.getMitigationID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMitigation))
            )
            .andExpect(status().isOk());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);
        Mitigation testMitigation = mitigationList.get(mitigationList.size() - 1);
        assertThat(testMitigation.getControlID()).isEqualTo(UPDATED_CONTROL_ID);
        assertThat(testMitigation.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testMitigation.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMitigation.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository).save(testMitigation);
    }

    @Test
    @Transactional
    void putNonExistingMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mitigation.getMitigationID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mitigation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void putWithIdMismatchMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mitigation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mitigation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void partialUpdateMitigationWithPatch() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();

        // Update the mitigation using partial update
        Mitigation partialUpdatedMitigation = new Mitigation();
        partialUpdatedMitigation.setMitigationID(mitigation.getMitigationID());

        partialUpdatedMitigation.reference(UPDATED_REFERENCE).type(UPDATED_TYPE).status(UPDATED_STATUS);

        restMitigationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMitigation.getMitigationID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMitigation))
            )
            .andExpect(status().isOk());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);
        Mitigation testMitigation = mitigationList.get(mitigationList.size() - 1);
        assertThat(testMitigation.getControlID()).isEqualTo(DEFAULT_CONTROL_ID);
        assertThat(testMitigation.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testMitigation.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMitigation.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateMitigationWithPatch() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();

        // Update the mitigation using partial update
        Mitigation partialUpdatedMitigation = new Mitigation();
        partialUpdatedMitigation.setMitigationID(mitigation.getMitigationID());

        partialUpdatedMitigation.controlID(UPDATED_CONTROL_ID).reference(UPDATED_REFERENCE).type(UPDATED_TYPE).status(UPDATED_STATUS);

        restMitigationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMitigation.getMitigationID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMitigation))
            )
            .andExpect(status().isOk());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);
        Mitigation testMitigation = mitigationList.get(mitigationList.size() - 1);
        assertThat(testMitigation.getControlID()).isEqualTo(UPDATED_CONTROL_ID);
        assertThat(testMitigation.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testMitigation.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testMitigation.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mitigation.getMitigationID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mitigation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mitigation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMitigation() throws Exception {
        int databaseSizeBeforeUpdate = mitigationRepository.findAll().size();
        mitigation.setMitigationID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMitigationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(mitigation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mitigation in the database
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(0)).save(mitigation);
    }

    @Test
    @Transactional
    void deleteMitigation() throws Exception {
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);

        int databaseSizeBeforeDelete = mitigationRepository.findAll().size();

        // Delete the mitigation
        restMitigationMockMvc
            .perform(delete(ENTITY_API_URL_ID, mitigation.getMitigationID().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mitigation> mitigationList = mitigationRepository.findAll();
        assertThat(mitigationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Mitigation in Elasticsearch
        verify(mockMitigationSearchRepository, times(1)).deleteById(mitigation.getMitigationID());
    }

    @Test
    @Transactional
    void searchMitigation() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        mitigationRepository.saveAndFlush(mitigation);
        when(mockMitigationSearchRepository.search("id:" + mitigation.getMitigationID())).thenReturn(Stream.of(mitigation));

        // Search the mitigation
        restMitigationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + mitigation.getMitigationID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].mitigationID").value(hasItem(mitigation.getMitigationID().toString())))
            .andExpect(jsonPath("$.[*].controlID").value(hasItem(DEFAULT_CONTROL_ID)))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
