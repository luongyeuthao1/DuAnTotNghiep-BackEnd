package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Apply;
import vn.fpt.domain.User;
import vn.fpt.domain.Post;
import vn.fpt.repository.ApplyRepository;
import vn.fpt.service.ApplyService;
import vn.fpt.service.dto.ApplyCriteria;
import vn.fpt.service.ApplyQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static vn.fpt.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ApplyResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ApplyResourceIT {

    private static final ZonedDateTime DEFAULT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private ApplyQueryService applyQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restApplyMockMvc;

    private Apply apply;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Apply createEntity(EntityManager em) {
        Apply apply = new Apply()
            .time(DEFAULT_TIME)
            .content(DEFAULT_CONTENT);
        return apply;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Apply createUpdatedEntity(EntityManager em) {
        Apply apply = new Apply()
            .time(UPDATED_TIME)
            .content(UPDATED_CONTENT);
        return apply;
    }

    @BeforeEach
    public void initTest() {
        apply = createEntity(em);
    }

    @Test
    @Transactional
    public void createApply() throws Exception {
        int databaseSizeBeforeCreate = applyRepository.findAll().size();
        // Create the Apply
        restApplyMockMvc.perform(post("/api/applies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(apply)))
            .andExpect(status().isCreated());

        // Validate the Apply in the database
        List<Apply> applyList = applyRepository.findAll();
        assertThat(applyList).hasSize(databaseSizeBeforeCreate + 1);
        Apply testApply = applyList.get(applyList.size() - 1);
        assertThat(testApply.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testApply.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createApplyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = applyRepository.findAll().size();

        // Create the Apply with an existing ID
        apply.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restApplyMockMvc.perform(post("/api/applies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(apply)))
            .andExpect(status().isBadRequest());

        // Validate the Apply in the database
        List<Apply> applyList = applyRepository.findAll();
        assertThat(applyList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllApplies() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList
        restApplyMockMvc.perform(get("/api/applies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apply.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }
    
    @Test
    @Transactional
    public void getApply() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get the apply
        restApplyMockMvc.perform(get("/api/applies/{id}", apply.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(apply.getId().intValue()))
            .andExpect(jsonPath("$.time").value(sameInstant(DEFAULT_TIME)))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }


    @Test
    @Transactional
    public void getAppliesByIdFiltering() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        Long id = apply.getId();

        defaultApplyShouldBeFound("id.equals=" + id);
        defaultApplyShouldNotBeFound("id.notEquals=" + id);

        defaultApplyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultApplyShouldNotBeFound("id.greaterThan=" + id);

        defaultApplyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultApplyShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAppliesByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time equals to DEFAULT_TIME
        defaultApplyShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the applyList where time equals to UPDATED_TIME
        defaultApplyShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time not equals to DEFAULT_TIME
        defaultApplyShouldNotBeFound("time.notEquals=" + DEFAULT_TIME);

        // Get all the applyList where time not equals to UPDATED_TIME
        defaultApplyShouldBeFound("time.notEquals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time in DEFAULT_TIME or UPDATED_TIME
        defaultApplyShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the applyList where time equals to UPDATED_TIME
        defaultApplyShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time is not null
        defaultApplyShouldBeFound("time.specified=true");

        // Get all the applyList where time is null
        defaultApplyShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time is greater than or equal to DEFAULT_TIME
        defaultApplyShouldBeFound("time.greaterThanOrEqual=" + DEFAULT_TIME);

        // Get all the applyList where time is greater than or equal to UPDATED_TIME
        defaultApplyShouldNotBeFound("time.greaterThanOrEqual=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time is less than or equal to DEFAULT_TIME
        defaultApplyShouldBeFound("time.lessThanOrEqual=" + DEFAULT_TIME);

        // Get all the applyList where time is less than or equal to SMALLER_TIME
        defaultApplyShouldNotBeFound("time.lessThanOrEqual=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time is less than DEFAULT_TIME
        defaultApplyShouldNotBeFound("time.lessThan=" + DEFAULT_TIME);

        // Get all the applyList where time is less than UPDATED_TIME
        defaultApplyShouldBeFound("time.lessThan=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllAppliesByTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where time is greater than DEFAULT_TIME
        defaultApplyShouldNotBeFound("time.greaterThan=" + DEFAULT_TIME);

        // Get all the applyList where time is greater than SMALLER_TIME
        defaultApplyShouldBeFound("time.greaterThan=" + SMALLER_TIME);
    }


    @Test
    @Transactional
    public void getAllAppliesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content equals to DEFAULT_CONTENT
        defaultApplyShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the applyList where content equals to UPDATED_CONTENT
        defaultApplyShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllAppliesByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content not equals to DEFAULT_CONTENT
        defaultApplyShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the applyList where content not equals to UPDATED_CONTENT
        defaultApplyShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllAppliesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultApplyShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the applyList where content equals to UPDATED_CONTENT
        defaultApplyShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllAppliesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content is not null
        defaultApplyShouldBeFound("content.specified=true");

        // Get all the applyList where content is null
        defaultApplyShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllAppliesByContentContainsSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content contains DEFAULT_CONTENT
        defaultApplyShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the applyList where content contains UPDATED_CONTENT
        defaultApplyShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllAppliesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);

        // Get all the applyList where content does not contain DEFAULT_CONTENT
        defaultApplyShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the applyList where content does not contain UPDATED_CONTENT
        defaultApplyShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllAppliesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        apply.setUser(user);
        applyRepository.saveAndFlush(apply);
        Long userId = user.getId();

        // Get all the applyList where user equals to userId
        defaultApplyShouldBeFound("userId.equals=" + userId);

        // Get all the applyList where user equals to userId + 1
        defaultApplyShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllAppliesByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        applyRepository.saveAndFlush(apply);
        Post post = PostResourceIT.createEntity(em);
        em.persist(post);
        em.flush();
        apply.setPost(post);
        applyRepository.saveAndFlush(apply);
        Long postId = post.getId();

        // Get all the applyList where post equals to postId
        defaultApplyShouldBeFound("postId.equals=" + postId);

        // Get all the applyList where post equals to postId + 1
        defaultApplyShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultApplyShouldBeFound(String filter) throws Exception {
        restApplyMockMvc.perform(get("/api/applies?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(apply.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));

        // Check, that the count call also returns 1
        restApplyMockMvc.perform(get("/api/applies/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultApplyShouldNotBeFound(String filter) throws Exception {
        restApplyMockMvc.perform(get("/api/applies?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restApplyMockMvc.perform(get("/api/applies/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingApply() throws Exception {
        // Get the apply
        restApplyMockMvc.perform(get("/api/applies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateApply() throws Exception {
        // Initialize the database
        applyService.save(apply);

        int databaseSizeBeforeUpdate = applyRepository.findAll().size();

        // Update the apply
        Apply updatedApply = applyRepository.findById(apply.getId()).get();
        // Disconnect from session so that the updates on updatedApply are not directly saved in db
        em.detach(updatedApply);
        updatedApply
            .time(UPDATED_TIME)
            .content(UPDATED_CONTENT);

        restApplyMockMvc.perform(put("/api/applies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedApply)))
            .andExpect(status().isOk());

        // Validate the Apply in the database
        List<Apply> applyList = applyRepository.findAll();
        assertThat(applyList).hasSize(databaseSizeBeforeUpdate);
        Apply testApply = applyList.get(applyList.size() - 1);
        assertThat(testApply.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testApply.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingApply() throws Exception {
        int databaseSizeBeforeUpdate = applyRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restApplyMockMvc.perform(put("/api/applies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(apply)))
            .andExpect(status().isBadRequest());

        // Validate the Apply in the database
        List<Apply> applyList = applyRepository.findAll();
        assertThat(applyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteApply() throws Exception {
        // Initialize the database
        applyService.save(apply);

        int databaseSizeBeforeDelete = applyRepository.findAll().size();

        // Delete the apply
        restApplyMockMvc.perform(delete("/api/applies/{id}", apply.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Apply> applyList = applyRepository.findAll();
        assertThat(applyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
