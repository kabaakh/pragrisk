package com.gobr.pragrisk.web.rest;

import static com.gobr.pragrisk.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gobr.pragrisk.IntegrationTest;
import com.gobr.pragrisk.domain.Scenario;
import com.gobr.pragrisk.repository.ScenarioRepository;
import com.gobr.pragrisk.repository.search.ScenarioSearchRepository;
import java.math.BigDecimal;
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
 * Integration tests for the {@link ScenarioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ScenarioResourceIT {

    private static final UUID DEFAULT_ACTOR_FK = UUID.randomUUID();
    private static final UUID UPDATED_ACTOR_FK = UUID.randomUUID();

    private static final UUID DEFAULT_TECHNOLOGY_FK = UUID.randomUUID();
    private static final UUID UPDATED_TECHNOLOGY_FK = UUID.randomUUID();

    private static final UUID DEFAULT_VULNERABILITY_FK = UUID.randomUUID();
    private static final UUID UPDATED_VULNERABILITY_FK = UUID.randomUUID();

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PROBABILITY = new BigDecimal(1);
    private static final BigDecimal UPDATED_PROBABILITY = new BigDecimal(2);

    private static final BigDecimal DEFAULT_QONSEQUENCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_QONSEQUENCE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/scenarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{scenarioID}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/scenarios";

    @Autowired
    private ScenarioRepository scenarioRepository;

    /**
     * This repository is mocked in the com.gobr.pragrisk.repository.search test package.
     *
     * @see com.gobr.pragrisk.repository.search.ScenarioSearchRepositoryMockConfiguration
     */
    @Autowired
    private ScenarioSearchRepository mockScenarioSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScenarioMockMvc;

    private Scenario scenario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .actorFK(DEFAULT_ACTOR_FK)
            .technologyFK(DEFAULT_TECHNOLOGY_FK)
            .vulnerabilityFK(DEFAULT_VULNERABILITY_FK)
            .description(DEFAULT_DESCRIPTION)
            .probability(DEFAULT_PROBABILITY)
            .qonsequence(DEFAULT_QONSEQUENCE);
        return scenario;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createUpdatedEntity(EntityManager em) {
        Scenario scenario = new Scenario()
            .actorFK(UPDATED_ACTOR_FK)
            .technologyFK(UPDATED_TECHNOLOGY_FK)
            .vulnerabilityFK(UPDATED_VULNERABILITY_FK)
            .description(UPDATED_DESCRIPTION)
            .probability(UPDATED_PROBABILITY)
            .qonsequence(UPDATED_QONSEQUENCE);
        return scenario;
    }

    @BeforeEach
    public void initTest() {
        scenario = createEntity(em);
    }

    @Test
    @Transactional
    void createScenario() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();
        // Create the Scenario
        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isCreated());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getActorFK()).isEqualTo(DEFAULT_ACTOR_FK);
        assertThat(testScenario.getTechnologyFK()).isEqualTo(DEFAULT_TECHNOLOGY_FK);
        assertThat(testScenario.getVulnerabilityFK()).isEqualTo(DEFAULT_VULNERABILITY_FK);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScenario.getProbability()).isEqualByComparingTo(DEFAULT_PROBABILITY);
        assertThat(testScenario.getQonsequence()).isEqualByComparingTo(DEFAULT_QONSEQUENCE);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(1)).save(testScenario);
    }

    @Test
    @Transactional
    void createScenarioWithExistingId() throws Exception {
        // Create the Scenario with an existing ID
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeCreate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void checkActorFKIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setActorFK(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTechnologyFKIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setTechnologyFK(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVulnerabilityFKIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setVulnerabilityFK(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isBadRequest());

        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScenarios() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarioList
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=scenarioID,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].scenarioID").value(hasItem(scenario.getScenarioID().toString())))
            .andExpect(jsonPath("$.[*].actorFK").value(hasItem(DEFAULT_ACTOR_FK.toString())))
            .andExpect(jsonPath("$.[*].technologyFK").value(hasItem(DEFAULT_TECHNOLOGY_FK.toString())))
            .andExpect(jsonPath("$.[*].vulnerabilityFK").value(hasItem(DEFAULT_VULNERABILITY_FK.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].probability").value(hasItem(sameNumber(DEFAULT_PROBABILITY))))
            .andExpect(jsonPath("$.[*].qonsequence").value(hasItem(sameNumber(DEFAULT_QONSEQUENCE))));
    }

    @Test
    @Transactional
    void getScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get the scenario
        restScenarioMockMvc
            .perform(get(ENTITY_API_URL_ID, scenario.getScenarioID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.scenarioID").value(scenario.getScenarioID().toString()))
            .andExpect(jsonPath("$.actorFK").value(DEFAULT_ACTOR_FK.toString()))
            .andExpect(jsonPath("$.technologyFK").value(DEFAULT_TECHNOLOGY_FK.toString()))
            .andExpect(jsonPath("$.vulnerabilityFK").value(DEFAULT_VULNERABILITY_FK.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.probability").value(sameNumber(DEFAULT_PROBABILITY)))
            .andExpect(jsonPath("$.qonsequence").value(sameNumber(DEFAULT_QONSEQUENCE)));
    }

    @Test
    @Transactional
    void getNonExistingScenario() throws Exception {
        // Get the scenario
        restScenarioMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findById(scenario.getScenarioID()).get();
        // Disconnect from session so that the updates on updatedScenario are not directly saved in db
        em.detach(updatedScenario);
        updatedScenario
            .actorFK(UPDATED_ACTOR_FK)
            .technologyFK(UPDATED_TECHNOLOGY_FK)
            .vulnerabilityFK(UPDATED_VULNERABILITY_FK)
            .description(UPDATED_DESCRIPTION)
            .probability(UPDATED_PROBABILITY)
            .qonsequence(UPDATED_QONSEQUENCE);

        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedScenario.getScenarioID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getActorFK()).isEqualTo(UPDATED_ACTOR_FK);
        assertThat(testScenario.getTechnologyFK()).isEqualTo(UPDATED_TECHNOLOGY_FK);
        assertThat(testScenario.getVulnerabilityFK()).isEqualTo(UPDATED_VULNERABILITY_FK);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.getProbability()).isEqualByComparingTo(UPDATED_PROBABILITY);
        assertThat(testScenario.getQonsequence()).isEqualByComparingTo(UPDATED_QONSEQUENCE);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository).save(testScenario);
    }

    @Test
    @Transactional
    void putNonExistingScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scenario.getScenarioID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void putWithIdMismatchScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void partialUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setScenarioID(scenario.getScenarioID());

        partialUpdatedScenario
            .vulnerabilityFK(UPDATED_VULNERABILITY_FK)
            .description(UPDATED_DESCRIPTION)
            .probability(UPDATED_PROBABILITY)
            .qonsequence(UPDATED_QONSEQUENCE);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getScenarioID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getActorFK()).isEqualTo(DEFAULT_ACTOR_FK);
        assertThat(testScenario.getTechnologyFK()).isEqualTo(DEFAULT_TECHNOLOGY_FK);
        assertThat(testScenario.getVulnerabilityFK()).isEqualTo(UPDATED_VULNERABILITY_FK);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.getProbability()).isEqualByComparingTo(UPDATED_PROBABILITY);
        assertThat(testScenario.getQonsequence()).isEqualByComparingTo(UPDATED_QONSEQUENCE);
    }

    @Test
    @Transactional
    void fullUpdateScenarioWithPatch() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario using partial update
        Scenario partialUpdatedScenario = new Scenario();
        partialUpdatedScenario.setScenarioID(scenario.getScenarioID());

        partialUpdatedScenario
            .actorFK(UPDATED_ACTOR_FK)
            .technologyFK(UPDATED_TECHNOLOGY_FK)
            .vulnerabilityFK(UPDATED_VULNERABILITY_FK)
            .description(UPDATED_DESCRIPTION)
            .probability(UPDATED_PROBABILITY)
            .qonsequence(UPDATED_QONSEQUENCE);

        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScenario.getScenarioID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScenario))
            )
            .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarioList.get(scenarioList.size() - 1);
        assertThat(testScenario.getActorFK()).isEqualTo(UPDATED_ACTOR_FK);
        assertThat(testScenario.getTechnologyFK()).isEqualTo(UPDATED_TECHNOLOGY_FK);
        assertThat(testScenario.getVulnerabilityFK()).isEqualTo(UPDATED_VULNERABILITY_FK);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.getProbability()).isEqualByComparingTo(UPDATED_PROBABILITY);
        assertThat(testScenario.getQonsequence()).isEqualByComparingTo(UPDATED_QONSEQUENCE);
    }

    @Test
    @Transactional
    void patchNonExistingScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scenario.getScenarioID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scenario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScenario() throws Exception {
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();
        scenario.setScenarioID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScenarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(scenario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Scenario in the database
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(0)).save(scenario);
    }

    @Test
    @Transactional
    void deleteScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        int databaseSizeBeforeDelete = scenarioRepository.findAll().size();

        // Delete the scenario
        restScenarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, scenario.getScenarioID().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertThat(scenarioList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Scenario in Elasticsearch
        verify(mockScenarioSearchRepository, times(1)).deleteById(scenario.getScenarioID());
    }

    @Test
    @Transactional
    void searchScenario() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        when(mockScenarioSearchRepository.search("id:" + scenario.getScenarioID())).thenReturn(Stream.of(scenario));

        // Search the scenario
        restScenarioMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + scenario.getScenarioID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].scenarioID").value(hasItem(scenario.getScenarioID().toString())))
            .andExpect(jsonPath("$.[*].actorFK").value(hasItem(DEFAULT_ACTOR_FK.toString())))
            .andExpect(jsonPath("$.[*].technologyFK").value(hasItem(DEFAULT_TECHNOLOGY_FK.toString())))
            .andExpect(jsonPath("$.[*].vulnerabilityFK").value(hasItem(DEFAULT_VULNERABILITY_FK.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].probability").value(hasItem(sameNumber(DEFAULT_PROBABILITY))))
            .andExpect(jsonPath("$.[*].qonsequence").value(hasItem(sameNumber(DEFAULT_QONSEQUENCE))));
    }
}
