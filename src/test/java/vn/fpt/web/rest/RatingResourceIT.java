package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Rating;
import vn.fpt.domain.User;
import vn.fpt.repository.RatingRepository;
import vn.fpt.service.RatingService;
import vn.fpt.service.dto.RatingCriteria;
import vn.fpt.service.RatingQueryService;

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
 * Integration tests for the {@link RatingResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class RatingResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_TIMES = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIMES = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIMES = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;
    private static final Integer SMALLER_RANK = 1 - 1;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RatingQueryService ratingQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRatingMockMvc;

    private Rating rating;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createEntity(EntityManager em) {
        Rating rating = new Rating()
            .content(DEFAULT_CONTENT)
            .times(DEFAULT_TIMES)
            .rank(DEFAULT_RANK);
        return rating;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createUpdatedEntity(EntityManager em) {
        Rating rating = new Rating()
            .content(UPDATED_CONTENT)
            .times(UPDATED_TIMES)
            .rank(UPDATED_RANK);
        return rating;
    }

    @BeforeEach
    public void initTest() {
        rating = createEntity(em);
    }

    @Test
    @Transactional
    public void createRating() throws Exception {
        int databaseSizeBeforeCreate = ratingRepository.findAll().size();
        // Create the Rating
        restRatingMockMvc.perform(post("/api/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rating)))
            .andExpect(status().isCreated());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate + 1);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testRating.getTimes()).isEqualTo(DEFAULT_TIMES);
        assertThat(testRating.getRank()).isEqualTo(DEFAULT_RANK);
    }

    @Test
    @Transactional
    public void createRatingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ratingRepository.findAll().size();

        // Create the Rating with an existing ID
        rating.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRatingMockMvc.perform(post("/api/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rating)))
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRatings() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList
        restRatingMockMvc.perform(get("/api/ratings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rating.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].times").value(hasItem(sameInstant(DEFAULT_TIMES))))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));
    }
    
    @Test
    @Transactional
    public void getRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get the rating
        restRatingMockMvc.perform(get("/api/ratings/{id}", rating.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rating.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.times").value(sameInstant(DEFAULT_TIMES)))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK));
    }


    @Test
    @Transactional
    public void getRatingsByIdFiltering() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        Long id = rating.getId();

        defaultRatingShouldBeFound("id.equals=" + id);
        defaultRatingShouldNotBeFound("id.notEquals=" + id);

        defaultRatingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRatingShouldNotBeFound("id.greaterThan=" + id);

        defaultRatingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRatingShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRatingsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content equals to DEFAULT_CONTENT
        defaultRatingShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the ratingList where content equals to UPDATED_CONTENT
        defaultRatingShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllRatingsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content not equals to DEFAULT_CONTENT
        defaultRatingShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the ratingList where content not equals to UPDATED_CONTENT
        defaultRatingShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllRatingsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultRatingShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the ratingList where content equals to UPDATED_CONTENT
        defaultRatingShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllRatingsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content is not null
        defaultRatingShouldBeFound("content.specified=true");

        // Get all the ratingList where content is null
        defaultRatingShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllRatingsByContentContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content contains DEFAULT_CONTENT
        defaultRatingShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the ratingList where content contains UPDATED_CONTENT
        defaultRatingShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllRatingsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where content does not contain DEFAULT_CONTENT
        defaultRatingShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the ratingList where content does not contain UPDATED_CONTENT
        defaultRatingShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllRatingsByTimesIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times equals to DEFAULT_TIMES
        defaultRatingShouldBeFound("times.equals=" + DEFAULT_TIMES);

        // Get all the ratingList where times equals to UPDATED_TIMES
        defaultRatingShouldNotBeFound("times.equals=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times not equals to DEFAULT_TIMES
        defaultRatingShouldNotBeFound("times.notEquals=" + DEFAULT_TIMES);

        // Get all the ratingList where times not equals to UPDATED_TIMES
        defaultRatingShouldBeFound("times.notEquals=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times in DEFAULT_TIMES or UPDATED_TIMES
        defaultRatingShouldBeFound("times.in=" + DEFAULT_TIMES + "," + UPDATED_TIMES);

        // Get all the ratingList where times equals to UPDATED_TIMES
        defaultRatingShouldNotBeFound("times.in=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times is not null
        defaultRatingShouldBeFound("times.specified=true");

        // Get all the ratingList where times is null
        defaultRatingShouldNotBeFound("times.specified=false");
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times is greater than or equal to DEFAULT_TIMES
        defaultRatingShouldBeFound("times.greaterThanOrEqual=" + DEFAULT_TIMES);

        // Get all the ratingList where times is greater than or equal to UPDATED_TIMES
        defaultRatingShouldNotBeFound("times.greaterThanOrEqual=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times is less than or equal to DEFAULT_TIMES
        defaultRatingShouldBeFound("times.lessThanOrEqual=" + DEFAULT_TIMES);

        // Get all the ratingList where times is less than or equal to SMALLER_TIMES
        defaultRatingShouldNotBeFound("times.lessThanOrEqual=" + SMALLER_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsLessThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times is less than DEFAULT_TIMES
        defaultRatingShouldNotBeFound("times.lessThan=" + DEFAULT_TIMES);

        // Get all the ratingList where times is less than UPDATED_TIMES
        defaultRatingShouldBeFound("times.lessThan=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllRatingsByTimesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where times is greater than DEFAULT_TIMES
        defaultRatingShouldNotBeFound("times.greaterThan=" + DEFAULT_TIMES);

        // Get all the ratingList where times is greater than SMALLER_TIMES
        defaultRatingShouldBeFound("times.greaterThan=" + SMALLER_TIMES);
    }


    @Test
    @Transactional
    public void getAllRatingsByRankIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank equals to DEFAULT_RANK
        defaultRatingShouldBeFound("rank.equals=" + DEFAULT_RANK);

        // Get all the ratingList where rank equals to UPDATED_RANK
        defaultRatingShouldNotBeFound("rank.equals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsNotEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank not equals to DEFAULT_RANK
        defaultRatingShouldNotBeFound("rank.notEquals=" + DEFAULT_RANK);

        // Get all the ratingList where rank not equals to UPDATED_RANK
        defaultRatingShouldBeFound("rank.notEquals=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank in DEFAULT_RANK or UPDATED_RANK
        defaultRatingShouldBeFound("rank.in=" + DEFAULT_RANK + "," + UPDATED_RANK);

        // Get all the ratingList where rank equals to UPDATED_RANK
        defaultRatingShouldNotBeFound("rank.in=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank is not null
        defaultRatingShouldBeFound("rank.specified=true");

        // Get all the ratingList where rank is null
        defaultRatingShouldNotBeFound("rank.specified=false");
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank is greater than or equal to DEFAULT_RANK
        defaultRatingShouldBeFound("rank.greaterThanOrEqual=" + DEFAULT_RANK);

        // Get all the ratingList where rank is greater than or equal to UPDATED_RANK
        defaultRatingShouldNotBeFound("rank.greaterThanOrEqual=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank is less than or equal to DEFAULT_RANK
        defaultRatingShouldBeFound("rank.lessThanOrEqual=" + DEFAULT_RANK);

        // Get all the ratingList where rank is less than or equal to SMALLER_RANK
        defaultRatingShouldNotBeFound("rank.lessThanOrEqual=" + SMALLER_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsLessThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank is less than DEFAULT_RANK
        defaultRatingShouldNotBeFound("rank.lessThan=" + DEFAULT_RANK);

        // Get all the ratingList where rank is less than UPDATED_RANK
        defaultRatingShouldBeFound("rank.lessThan=" + UPDATED_RANK);
    }

    @Test
    @Transactional
    public void getAllRatingsByRankIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where rank is greater than DEFAULT_RANK
        defaultRatingShouldNotBeFound("rank.greaterThan=" + DEFAULT_RANK);

        // Get all the ratingList where rank is greater than SMALLER_RANK
        defaultRatingShouldBeFound("rank.greaterThan=" + SMALLER_RANK);
    }


    @Test
    @Transactional
    public void getAllRatingsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        rating.setUser(user);
        ratingRepository.saveAndFlush(rating);
        Long userId = user.getId();

        // Get all the ratingList where user equals to userId
        defaultRatingShouldBeFound("userId.equals=" + userId);

        // Get all the ratingList where user equals to userId + 1
        defaultRatingShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRatingShouldBeFound(String filter) throws Exception {
        restRatingMockMvc.perform(get("/api/ratings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rating.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].times").value(hasItem(sameInstant(DEFAULT_TIMES))))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)));

        // Check, that the count call also returns 1
        restRatingMockMvc.perform(get("/api/ratings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRatingShouldNotBeFound(String filter) throws Exception {
        restRatingMockMvc.perform(get("/api/ratings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRatingMockMvc.perform(get("/api/ratings/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingRating() throws Exception {
        // Get the rating
        restRatingMockMvc.perform(get("/api/ratings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRating() throws Exception {
        // Initialize the database
        ratingService.save(rating);

        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();

        // Update the rating
        Rating updatedRating = ratingRepository.findById(rating.getId()).get();
        // Disconnect from session so that the updates on updatedRating are not directly saved in db
        em.detach(updatedRating);
        updatedRating
            .content(UPDATED_CONTENT)
            .times(UPDATED_TIMES)
            .rank(UPDATED_RANK);

        restRatingMockMvc.perform(put("/api/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRating)))
            .andExpect(status().isOk());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testRating.getTimes()).isEqualTo(UPDATED_TIMES);
        assertThat(testRating.getRank()).isEqualTo(UPDATED_RANK);
    }

    @Test
    @Transactional
    public void updateNonExistingRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRatingMockMvc.perform(put("/api/ratings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rating)))
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRating() throws Exception {
        // Initialize the database
        ratingService.save(rating);

        int databaseSizeBeforeDelete = ratingRepository.findAll().size();

        // Delete the rating
        restRatingMockMvc.perform(delete("/api/ratings/{id}", rating.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
