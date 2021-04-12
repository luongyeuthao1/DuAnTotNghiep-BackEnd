package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Comment;
import vn.fpt.domain.User;
import vn.fpt.domain.Post;
import vn.fpt.repository.CommentRepository;
import vn.fpt.service.CommentService;
import vn.fpt.service.dto.CommentCriteria;
import vn.fpt.service.CommentQueryService;

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
 * Integration tests for the {@link CommentResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class CommentResourceIT {

    private static final ZonedDateTime DEFAULT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentQueryService commentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentMockMvc;

    private Comment comment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity(EntityManager em) {
        Comment comment = new Comment()
            .time(DEFAULT_TIME)
            .content(DEFAULT_CONTENT);
        return comment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createUpdatedEntity(EntityManager em) {
        Comment comment = new Comment()
            .time(UPDATED_TIME)
            .content(UPDATED_CONTENT);
        return comment;
    }

    @BeforeEach
    public void initTest() {
        comment = createEntity(em);
    }

    @Test
    @Transactional
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        // Create the Comment
        restCommentMockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(comment)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getTime()).isEqualTo(DEFAULT_TIME);
        assertThat(testComment.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createCommentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // Create the Comment with an existing ID
        comment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc.perform(post("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(comment)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }
    
    @Test
    @Transactional
    public void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
            .andExpect(jsonPath("$.time").value(sameInstant(DEFAULT_TIME)))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }


    @Test
    @Transactional
    public void getCommentsByIdFiltering() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        Long id = comment.getId();

        defaultCommentShouldBeFound("id.equals=" + id);
        defaultCommentShouldNotBeFound("id.notEquals=" + id);

        defaultCommentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommentShouldNotBeFound("id.greaterThan=" + id);

        defaultCommentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCommentsByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time equals to DEFAULT_TIME
        defaultCommentShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the commentList where time equals to UPDATED_TIME
        defaultCommentShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time not equals to DEFAULT_TIME
        defaultCommentShouldNotBeFound("time.notEquals=" + DEFAULT_TIME);

        // Get all the commentList where time not equals to UPDATED_TIME
        defaultCommentShouldBeFound("time.notEquals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time in DEFAULT_TIME or UPDATED_TIME
        defaultCommentShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the commentList where time equals to UPDATED_TIME
        defaultCommentShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time is not null
        defaultCommentShouldBeFound("time.specified=true");

        // Get all the commentList where time is null
        defaultCommentShouldNotBeFound("time.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time is greater than or equal to DEFAULT_TIME
        defaultCommentShouldBeFound("time.greaterThanOrEqual=" + DEFAULT_TIME);

        // Get all the commentList where time is greater than or equal to UPDATED_TIME
        defaultCommentShouldNotBeFound("time.greaterThanOrEqual=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time is less than or equal to DEFAULT_TIME
        defaultCommentShouldBeFound("time.lessThanOrEqual=" + DEFAULT_TIME);

        // Get all the commentList where time is less than or equal to SMALLER_TIME
        defaultCommentShouldNotBeFound("time.lessThanOrEqual=" + SMALLER_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time is less than DEFAULT_TIME
        defaultCommentShouldNotBeFound("time.lessThan=" + DEFAULT_TIME);

        // Get all the commentList where time is less than UPDATED_TIME
        defaultCommentShouldBeFound("time.lessThan=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllCommentsByTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where time is greater than DEFAULT_TIME
        defaultCommentShouldNotBeFound("time.greaterThan=" + DEFAULT_TIME);

        // Get all the commentList where time is greater than SMALLER_TIME
        defaultCommentShouldBeFound("time.greaterThan=" + SMALLER_TIME);
    }


    @Test
    @Transactional
    public void getAllCommentsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content equals to DEFAULT_CONTENT
        defaultCommentShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the commentList where content equals to UPDATED_CONTENT
        defaultCommentShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllCommentsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content not equals to DEFAULT_CONTENT
        defaultCommentShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the commentList where content not equals to UPDATED_CONTENT
        defaultCommentShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllCommentsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultCommentShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the commentList where content equals to UPDATED_CONTENT
        defaultCommentShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllCommentsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content is not null
        defaultCommentShouldBeFound("content.specified=true");

        // Get all the commentList where content is null
        defaultCommentShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllCommentsByContentContainsSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content contains DEFAULT_CONTENT
        defaultCommentShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the commentList where content contains UPDATED_CONTENT
        defaultCommentShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllCommentsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where content does not contain DEFAULT_CONTENT
        defaultCommentShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the commentList where content does not contain UPDATED_CONTENT
        defaultCommentShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllCommentsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        comment.setUser(user);
        commentRepository.saveAndFlush(comment);
        Long userId = user.getId();

        // Get all the commentList where user equals to userId
        defaultCommentShouldBeFound("userId.equals=" + userId);

        // Get all the commentList where user equals to userId + 1
        defaultCommentShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllCommentsByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        Post post = PostResourceIT.createEntity(em);
        em.persist(post);
        em.flush();
        comment.setPost(post);
        commentRepository.saveAndFlush(comment);
        Long postId = post.getId();

        // Get all the commentList where post equals to postId
        defaultCommentShouldBeFound("postId.equals=" + postId);

        // Get all the commentList where post equals to postId + 1
        defaultCommentShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommentShouldBeFound(String filter) throws Exception {
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(sameInstant(DEFAULT_TIME))))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));

        // Check, that the count call also returns 1
        restCommentMockMvc.perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommentShouldNotBeFound(String filter) throws Exception {
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommentMockMvc.perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        // Initialize the database
        commentService.save(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).get();
        // Disconnect from session so that the updates on updatedComment are not directly saved in db
        em.detach(updatedComment);
        updatedComment
            .time(UPDATED_TIME)
            .content(UPDATED_CONTENT);

        restCommentMockMvc.perform(put("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedComment)))
            .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getTime()).isEqualTo(UPDATED_TIME);
        assertThat(testComment.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc.perform(put("/api/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(comment)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception {
        // Initialize the database
        commentService.save(comment);

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // Delete the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
