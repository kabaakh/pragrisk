package com.gobr.pragrisk.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gobr.pragrisk.IntegrationTest;
import com.gobr.pragrisk.domain.Actor;
import com.gobr.pragrisk.domain.enumeration.Environment;
import com.gobr.pragrisk.repository.ActorRepository;
import com.gobr.pragrisk.repository.search.ActorSearchRepository;
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
 * Integration tests for the {@link ActorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ActorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NICK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NICK_NAME = "BBBBBBBBBB";

    private static final Environment DEFAULT_ENVIRON_MENT = Environment.KOM;
    private static final Environment UPDATED_ENVIRON_MENT = Environment.KS;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{actorID}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/actors";

    @Autowired
    private ActorRepository actorRepository;

    /**
     * This repository is mocked in the com.gobr.pragrisk.repository.search test package.
     *
     * @see com.gobr.pragrisk.repository.search.ActorSearchRepositoryMockConfiguration
     */
    @Autowired
    private ActorSearchRepository mockActorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActorMockMvc;

    private Actor actor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createEntity(EntityManager em) {
        Actor actor = new Actor()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .nickName(DEFAULT_NICK_NAME)
            .environMent(DEFAULT_ENVIRON_MENT)
            .description(DEFAULT_DESCRIPTION);
        return actor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Actor createUpdatedEntity(EntityManager em) {
        Actor actor = new Actor()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .nickName(UPDATED_NICK_NAME)
            .environMent(UPDATED_ENVIRON_MENT)
            .description(UPDATED_DESCRIPTION);
        return actor;
    }

    @BeforeEach
    public void initTest() {
        actor = createEntity(em);
    }

    @Test
    @Transactional
    void createActor() throws Exception {
        int databaseSizeBeforeCreate = actorRepository.findAll().size();
        // Create the Actor
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isCreated());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate + 1);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getNickName()).isEqualTo(DEFAULT_NICK_NAME);
        assertThat(testActor.getEnvironMent()).isEqualTo(DEFAULT_ENVIRON_MENT);
        assertThat(testActor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(1)).save(testActor);
    }

    @Test
    @Transactional
    void createActorWithExistingId() throws Exception {
        // Create the Actor with an existing ID
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeCreate = actorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setFirstName(null);

        // Create the Actor, which fails.

        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setLastName(null);

        // Create the Actor, which fails.

        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNickNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setNickName(null);

        // Create the Actor, which fails.

        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEnvironMentIsRequired() throws Exception {
        int databaseSizeBeforeTest = actorRepository.findAll().size();
        // set the field null
        actor.setEnvironMent(null);

        // Create the Actor, which fails.

        restActorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isBadRequest());

        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllActors() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get all the actorList
        restActorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=actorID,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].actorID").value(hasItem(actor.getActorID().toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].nickName").value(hasItem(DEFAULT_NICK_NAME)))
            .andExpect(jsonPath("$.[*].environMent").value(hasItem(DEFAULT_ENVIRON_MENT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        // Get the actor
        restActorMockMvc
            .perform(get(ENTITY_API_URL_ID, actor.getActorID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.actorID").value(actor.getActorID().toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.nickName").value(DEFAULT_NICK_NAME))
            .andExpect(jsonPath("$.environMent").value(DEFAULT_ENVIRON_MENT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingActor() throws Exception {
        // Get the actor
        restActorMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor
        Actor updatedActor = actorRepository.findById(actor.getActorID()).get();
        // Disconnect from session so that the updates on updatedActor are not directly saved in db
        em.detach(updatedActor);
        updatedActor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .nickName(UPDATED_NICK_NAME)
            .environMent(UPDATED_ENVIRON_MENT)
            .description(UPDATED_DESCRIPTION);

        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActor.getActorID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getNickName()).isEqualTo(UPDATED_NICK_NAME);
        assertThat(testActor.getEnvironMent()).isEqualTo(UPDATED_ENVIRON_MENT);
        assertThat(testActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository).save(testActor);
    }

    @Test
    @Transactional
    void putNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, actor.getActorID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void putWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void partialUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setActorID(actor.getActorID());

        partialUpdatedActor
            .firstName(UPDATED_FIRST_NAME)
            .nickName(UPDATED_NICK_NAME)
            .environMent(UPDATED_ENVIRON_MENT)
            .description(UPDATED_DESCRIPTION);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getActorID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testActor.getNickName()).isEqualTo(UPDATED_NICK_NAME);
        assertThat(testActor.getEnvironMent()).isEqualTo(UPDATED_ENVIRON_MENT);
        assertThat(testActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateActorWithPatch() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeUpdate = actorRepository.findAll().size();

        // Update the actor using partial update
        Actor partialUpdatedActor = new Actor();
        partialUpdatedActor.setActorID(actor.getActorID());

        partialUpdatedActor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .nickName(UPDATED_NICK_NAME)
            .environMent(UPDATED_ENVIRON_MENT)
            .description(UPDATED_DESCRIPTION);

        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActor.getActorID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActor))
            )
            .andExpect(status().isOk());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);
        Actor testActor = actorList.get(actorList.size() - 1);
        assertThat(testActor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testActor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testActor.getNickName()).isEqualTo(UPDATED_NICK_NAME);
        assertThat(testActor.getEnvironMent()).isEqualTo(UPDATED_ENVIRON_MENT);
        assertThat(testActor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, actor.getActorID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(actor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActor() throws Exception {
        int databaseSizeBeforeUpdate = actorRepository.findAll().size();
        actor.setActorID(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(actor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Actor in the database
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(0)).save(actor);
    }

    @Test
    @Transactional
    void deleteActor() throws Exception {
        // Initialize the database
        actorRepository.saveAndFlush(actor);

        int databaseSizeBeforeDelete = actorRepository.findAll().size();

        // Delete the actor
        restActorMockMvc
            .perform(delete(ENTITY_API_URL_ID, actor.getActorID().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Actor> actorList = actorRepository.findAll();
        assertThat(actorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Actor in Elasticsearch
        verify(mockActorSearchRepository, times(1)).deleteById(actor.getActorID());
    }

    @Test
    @Transactional
    void searchActor() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        actorRepository.saveAndFlush(actor);
        when(mockActorSearchRepository.search("id:" + actor.getActorID())).thenReturn(Stream.of(actor));

        // Search the actor
        restActorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + actor.getActorID()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].actorID").value(hasItem(actor.getActorID().toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].nickName").value(hasItem(DEFAULT_NICK_NAME)))
            .andExpect(jsonPath("$.[*].environMent").value(hasItem(DEFAULT_ENVIRON_MENT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
