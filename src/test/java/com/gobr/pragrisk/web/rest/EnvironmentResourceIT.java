package com.gobr.pragrisk.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gobr.pragrisk.IntegrationTest;
import com.gobr.pragrisk.domain.Environment;
import com.gobr.pragrisk.repository.EnvironmentRepository;
import com.gobr.pragrisk.repository.search.EnvironmentSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link EnvironmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnvironmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/environments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/environments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnvironmentRepository environmentRepository;

    /**
     * This repository is mocked in the com.gobr.pragrisk.repository.search test package.
     *
     * @see com.gobr.pragrisk.repository.search.EnvironmentSearchRepositoryMockConfiguration
     */
    @Autowired
    private EnvironmentSearchRepository mockEnvironmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentMockMvc;

    private Environment environment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createEntity(EntityManager em) {
        Environment environment = new Environment().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return environment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Environment createUpdatedEntity(EntityManager em) {
        Environment environment = new Environment().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return environment;
    }

    @BeforeEach
    public void initTest() {
        environment = createEntity(em);
    }

    @Test
    @Transactional
    void createEnvironment() throws Exception {
        int databaseSizeBeforeCreate = environmentRepository.findAll().size();
        // Create the Environment
        restEnvironmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environment)))
            .andExpect(status().isCreated());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate + 1);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(1)).save(testEnvironment);
    }

    @Test
    @Transactional
    void createEnvironmentWithExistingId() throws Exception {
        // Create the Environment with an existing ID
        environment.setId(1L);

        int databaseSizeBeforeCreate = environmentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environment)))
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = environmentRepository.findAll().size();
        // set the field null
        environment.setName(null);

        // Create the Environment, which fails.

        restEnvironmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environment)))
            .andExpect(status().isBadRequest());

        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEnvironments() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get all the environmentList
        restEnvironmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        // Get the environment
        restEnvironmentMockMvc
            .perform(get(ENTITY_API_URL_ID, environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environment.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingEnvironment() throws Exception {
        // Get the environment
        restEnvironmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment
        Environment updatedEnvironment = environmentRepository.findById(environment.getId()).get();
        // Disconnect from session so that the updates on updatedEnvironment are not directly saved in db
        em.detach(updatedEnvironment);
        updatedEnvironment.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnvironment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEnvironment))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository).save(testEnvironment);
    }

    @Test
    @Transactional
    void putNonExistingEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(environment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(environment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(environment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void partialUpdateEnvironmentWithPatch() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment using partial update
        Environment partialUpdatedEnvironment = new Environment();
        partialUpdatedEnvironment.setId(environment.getId());

        partialUpdatedEnvironment.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnvironment))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateEnvironmentWithPatch() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();

        // Update the environment using partial update
        Environment partialUpdatedEnvironment = new Environment();
        partialUpdatedEnvironment.setId(environment.getId());

        partialUpdatedEnvironment.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEnvironment))
            )
            .andExpect(status().isOk());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);
        Environment testEnvironment = environmentList.get(environmentList.size() - 1);
        assertThat(testEnvironment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEnvironment.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, environment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(environment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(environment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvironment() throws Exception {
        int databaseSizeBeforeUpdate = environmentRepository.findAll().size();
        environment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(environment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Environment in the database
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(0)).save(environment);
    }

    @Test
    @Transactional
    void deleteEnvironment() throws Exception {
        // Initialize the database
        environmentRepository.saveAndFlush(environment);

        int databaseSizeBeforeDelete = environmentRepository.findAll().size();

        // Delete the environment
        restEnvironmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, environment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Environment> environmentList = environmentRepository.findAll();
        assertThat(environmentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Environment in Elasticsearch
        verify(mockEnvironmentSearchRepository, times(1)).deleteById(environment.getId());
    }

    @Test
    @Transactional
    void searchEnvironment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        environmentRepository.saveAndFlush(environment);
        when(mockEnvironmentSearchRepository.search("id:" + environment.getId())).thenReturn(Stream.of(environment));

        // Search the environment
        restEnvironmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + environment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environment.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
