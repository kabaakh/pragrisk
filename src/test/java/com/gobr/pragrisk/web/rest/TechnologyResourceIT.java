package com.gobr.pragrisk.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gobr.pragrisk.IntegrationTest;
import com.gobr.pragrisk.domain.Technology;
import com.gobr.pragrisk.domain.enumeration.TechCategory;
import com.gobr.pragrisk.domain.enumeration.TechStack;
import com.gobr.pragrisk.repository.TechnologyRepository;
import com.gobr.pragrisk.repository.search.TechnologySearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TechnologyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TechnologyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final TechCategory DEFAULT_CATEGORY = TechCategory.APPLICATION;
    private static final TechCategory UPDATED_CATEGORY = TechCategory.SERVICE;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TechStack DEFAULT_TECH_STACK_TYPE = TechStack.JAVA;
    private static final TechStack UPDATED_TECH_STACK_TYPE = TechStack.NET;

    private static final String ENTITY_API_URL = "/api/technologies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/technologies";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TechnologyRepository technologyRepository;

    /**
     * This repository is mocked in the com.gobr.pragrisk.repository.search test package.
     *
     * @see com.gobr.pragrisk.repository.search.TechnologySearchRepositoryMockConfiguration
     */
    @Autowired
    private TechnologySearchRepository mockTechnologySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTechnologyMockMvc;

    private Technology technology;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Technology createEntity(EntityManager em) {
        Technology technology = new Technology()
            .name(DEFAULT_NAME)
            .category(DEFAULT_CATEGORY)
            .description(DEFAULT_DESCRIPTION)
            .techStackType(DEFAULT_TECH_STACK_TYPE);
        return technology;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Technology createUpdatedEntity(EntityManager em) {
        Technology technology = new Technology()
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .techStackType(UPDATED_TECH_STACK_TYPE);
        return technology;
    }

    @BeforeEach
    public void initTest() {
        technology = createEntity(em);
    }

    @Test
    @Transactional
    void createTechnology() throws Exception {
        int databaseSizeBeforeCreate = technologyRepository.findAll().size();
        // Create the Technology
        restTechnologyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isCreated());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeCreate + 1);
        Technology testTechnology = technologyList.get(technologyList.size() - 1);
        assertThat(testTechnology.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTechnology.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTechnology.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTechnology.getTechStackType()).isEqualTo(DEFAULT_TECH_STACK_TYPE);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(1)).save(testTechnology);
    }

    @Test
    @Transactional
    void createTechnologyWithExistingId() throws Exception {
        // Create the Technology with an existing ID
        technology.setId(1L);

        int databaseSizeBeforeCreate = technologyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTechnologyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isBadRequest());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = technologyRepository.findAll().size();
        // set the field null
        technology.setName(null);

        // Create the Technology, which fails.

        restTechnologyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isBadRequest());

        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = technologyRepository.findAll().size();
        // set the field null
        technology.setCategory(null);

        // Create the Technology, which fails.

        restTechnologyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isBadRequest());

        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTechStackTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = technologyRepository.findAll().size();
        // set the field null
        technology.setTechStackType(null);

        // Create the Technology, which fails.

        restTechnologyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isBadRequest());

        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTechnologies() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        // Get all the technologyList
        restTechnologyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technology.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].techStackType").value(hasItem(DEFAULT_TECH_STACK_TYPE.toString())));
    }

    @Test
    @Transactional
    void getTechnology() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        // Get the technology
        restTechnologyMockMvc
            .perform(get(ENTITY_API_URL_ID, technology.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(technology.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.techStackType").value(DEFAULT_TECH_STACK_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTechnology() throws Exception {
        // Get the technology
        restTechnologyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTechnology() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();

        // Update the technology
        Technology updatedTechnology = technologyRepository.findById(technology.getId()).get();
        // Disconnect from session so that the updates on updatedTechnology are not directly saved in db
        em.detach(updatedTechnology);
        updatedTechnology
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .techStackType(UPDATED_TECH_STACK_TYPE);

        restTechnologyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTechnology.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTechnology))
            )
            .andExpect(status().isOk());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);
        Technology testTechnology = technologyList.get(technologyList.size() - 1);
        assertThat(testTechnology.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTechnology.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTechnology.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTechnology.getTechStackType()).isEqualTo(UPDATED_TECH_STACK_TYPE);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository).save(testTechnology);
    }

    @Test
    @Transactional
    void putNonExistingTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, technology.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(technology))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void putWithIdMismatchTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(technology))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(technology)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void partialUpdateTechnologyWithPatch() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();

        // Update the technology using partial update
        Technology partialUpdatedTechnology = new Technology();
        partialUpdatedTechnology.setId(technology.getId());

        partialUpdatedTechnology.description(UPDATED_DESCRIPTION);

        restTechnologyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTechnology.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechnology))
            )
            .andExpect(status().isOk());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);
        Technology testTechnology = technologyList.get(technologyList.size() - 1);
        assertThat(testTechnology.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTechnology.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTechnology.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTechnology.getTechStackType()).isEqualTo(DEFAULT_TECH_STACK_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTechnologyWithPatch() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();

        // Update the technology using partial update
        Technology partialUpdatedTechnology = new Technology();
        partialUpdatedTechnology.setId(technology.getId());

        partialUpdatedTechnology
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .techStackType(UPDATED_TECH_STACK_TYPE);

        restTechnologyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTechnology.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTechnology))
            )
            .andExpect(status().isOk());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);
        Technology testTechnology = technologyList.get(technologyList.size() - 1);
        assertThat(testTechnology.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTechnology.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTechnology.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTechnology.getTechStackType()).isEqualTo(UPDATED_TECH_STACK_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, technology.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(technology))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(technology))
            )
            .andExpect(status().isBadRequest());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTechnology() throws Exception {
        int databaseSizeBeforeUpdate = technologyRepository.findAll().size();
        technology.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTechnologyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(technology))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Technology in the database
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(0)).save(technology);
    }

    @Test
    @Transactional
    void deleteTechnology() throws Exception {
        // Initialize the database
        technologyRepository.saveAndFlush(technology);

        int databaseSizeBeforeDelete = technologyRepository.findAll().size();

        // Delete the technology
        restTechnologyMockMvc
            .perform(delete(ENTITY_API_URL_ID, technology.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Technology> technologyList = technologyRepository.findAll();
        assertThat(technologyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Technology in Elasticsearch
        verify(mockTechnologySearchRepository, times(1)).deleteById(technology.getId());
    }

    @Test
    @Transactional
    void searchTechnology() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        technologyRepository.saveAndFlush(technology);
        when(mockTechnologySearchRepository.search("id:" + technology.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(technology), PageRequest.of(0, 1), 1));

        // Search the technology
        restTechnologyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + technology.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technology.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].techStackType").value(hasItem(DEFAULT_TECH_STACK_TYPE.toString())));
    }
}
