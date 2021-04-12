package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Post;
import vn.fpt.domain.User;
import vn.fpt.repository.PostRepository;
import vn.fpt.service.PostService;
import vn.fpt.service.dto.PostCriteria;
import vn.fpt.service.PostQueryService;

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
 * Integration tests for the {@link PostResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class PostResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Integer DEFAULT_LIKE = 1;
    private static final Integer UPDATED_LIKE = 2;
    private static final Integer SMALLER_LIKE = 1 - 1;

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;
    private static final Integer SMALLER_TYPE = 1 - 1;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPostMockMvc;

    private Post post;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .content(DEFAULT_CONTENT)
            .createTime(DEFAULT_CREATE_TIME)
            .like(DEFAULT_LIKE)
            .type(DEFAULT_TYPE);
        return post;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity(EntityManager em) {
        Post post = new Post()
            .content(UPDATED_CONTENT)
            .createTime(UPDATED_CREATE_TIME)
            .like(UPDATED_LIKE)
            .type(UPDATED_TYPE);
        return post;
    }

    @BeforeEach
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();
        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testPost.getCreateTime()).isEqualTo(DEFAULT_CREATE_TIME);
        assertThat(testPost.getLike()).isEqualTo(DEFAULT_LIKE);
        assertThat(testPost.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        post.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].like").value(hasItem(DEFAULT_LIKE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }
    
    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
            .andExpect(jsonPath("$.like").value(DEFAULT_LIKE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }


    @Test
    @Transactional
    public void getPostsByIdFiltering() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        Long id = post.getId();

        defaultPostShouldBeFound("id.equals=" + id);
        defaultPostShouldNotBeFound("id.notEquals=" + id);

        defaultPostShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.greaterThan=" + id);

        defaultPostShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPostShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPostsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content equals to DEFAULT_CONTENT
        defaultPostShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content not equals to DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the postList where content not equals to UPDATED_CONTENT
        defaultPostShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultPostShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the postList where content equals to UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content is not null
        defaultPostShouldBeFound("content.specified=true");

        // Get all the postList where content is null
        defaultPostShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllPostsByContentContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content contains DEFAULT_CONTENT
        defaultPostShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the postList where content contains UPDATED_CONTENT
        defaultPostShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllPostsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where content does not contain DEFAULT_CONTENT
        defaultPostShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the postList where content does not contain UPDATED_CONTENT
        defaultPostShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime equals to DEFAULT_CREATE_TIME
        defaultPostShouldBeFound("createTime.equals=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime equals to UPDATED_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.equals=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime not equals to DEFAULT_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.notEquals=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime not equals to UPDATED_CREATE_TIME
        defaultPostShouldBeFound("createTime.notEquals=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime in DEFAULT_CREATE_TIME or UPDATED_CREATE_TIME
        defaultPostShouldBeFound("createTime.in=" + DEFAULT_CREATE_TIME + "," + UPDATED_CREATE_TIME);

        // Get all the postList where createTime equals to UPDATED_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.in=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime is not null
        defaultPostShouldBeFound("createTime.specified=true");

        // Get all the postList where createTime is null
        defaultPostShouldNotBeFound("createTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime is greater than or equal to DEFAULT_CREATE_TIME
        defaultPostShouldBeFound("createTime.greaterThanOrEqual=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime is greater than or equal to UPDATED_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.greaterThanOrEqual=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime is less than or equal to DEFAULT_CREATE_TIME
        defaultPostShouldBeFound("createTime.lessThanOrEqual=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime is less than or equal to SMALLER_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.lessThanOrEqual=" + SMALLER_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime is less than DEFAULT_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.lessThan=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime is less than UPDATED_CREATE_TIME
        defaultPostShouldBeFound("createTime.lessThan=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    public void getAllPostsByCreateTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where createTime is greater than DEFAULT_CREATE_TIME
        defaultPostShouldNotBeFound("createTime.greaterThan=" + DEFAULT_CREATE_TIME);

        // Get all the postList where createTime is greater than SMALLER_CREATE_TIME
        defaultPostShouldBeFound("createTime.greaterThan=" + SMALLER_CREATE_TIME);
    }


    @Test
    @Transactional
    public void getAllPostsByLikeIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like equals to DEFAULT_LIKE
        defaultPostShouldBeFound("like.equals=" + DEFAULT_LIKE);

        // Get all the postList where like equals to UPDATED_LIKE
        defaultPostShouldNotBeFound("like.equals=" + UPDATED_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like not equals to DEFAULT_LIKE
        defaultPostShouldNotBeFound("like.notEquals=" + DEFAULT_LIKE);

        // Get all the postList where like not equals to UPDATED_LIKE
        defaultPostShouldBeFound("like.notEquals=" + UPDATED_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like in DEFAULT_LIKE or UPDATED_LIKE
        defaultPostShouldBeFound("like.in=" + DEFAULT_LIKE + "," + UPDATED_LIKE);

        // Get all the postList where like equals to UPDATED_LIKE
        defaultPostShouldNotBeFound("like.in=" + UPDATED_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like is not null
        defaultPostShouldBeFound("like.specified=true");

        // Get all the postList where like is null
        defaultPostShouldNotBeFound("like.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like is greater than or equal to DEFAULT_LIKE
        defaultPostShouldBeFound("like.greaterThanOrEqual=" + DEFAULT_LIKE);

        // Get all the postList where like is greater than or equal to UPDATED_LIKE
        defaultPostShouldNotBeFound("like.greaterThanOrEqual=" + UPDATED_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like is less than or equal to DEFAULT_LIKE
        defaultPostShouldBeFound("like.lessThanOrEqual=" + DEFAULT_LIKE);

        // Get all the postList where like is less than or equal to SMALLER_LIKE
        defaultPostShouldNotBeFound("like.lessThanOrEqual=" + SMALLER_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like is less than DEFAULT_LIKE
        defaultPostShouldNotBeFound("like.lessThan=" + DEFAULT_LIKE);

        // Get all the postList where like is less than UPDATED_LIKE
        defaultPostShouldBeFound("like.lessThan=" + UPDATED_LIKE);
    }

    @Test
    @Transactional
    public void getAllPostsByLikeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where like is greater than DEFAULT_LIKE
        defaultPostShouldNotBeFound("like.greaterThan=" + DEFAULT_LIKE);

        // Get all the postList where like is greater than SMALLER_LIKE
        defaultPostShouldBeFound("like.greaterThan=" + SMALLER_LIKE);
    }


    @Test
    @Transactional
    public void getAllPostsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type equals to DEFAULT_TYPE
        defaultPostShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the postList where type equals to UPDATED_TYPE
        defaultPostShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type not equals to DEFAULT_TYPE
        defaultPostShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the postList where type not equals to UPDATED_TYPE
        defaultPostShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultPostShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the postList where type equals to UPDATED_TYPE
        defaultPostShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type is not null
        defaultPostShouldBeFound("type.specified=true");

        // Get all the postList where type is null
        defaultPostShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type is greater than or equal to DEFAULT_TYPE
        defaultPostShouldBeFound("type.greaterThanOrEqual=" + DEFAULT_TYPE);

        // Get all the postList where type is greater than or equal to UPDATED_TYPE
        defaultPostShouldNotBeFound("type.greaterThanOrEqual=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type is less than or equal to DEFAULT_TYPE
        defaultPostShouldBeFound("type.lessThanOrEqual=" + DEFAULT_TYPE);

        // Get all the postList where type is less than or equal to SMALLER_TYPE
        defaultPostShouldNotBeFound("type.lessThanOrEqual=" + SMALLER_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsLessThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type is less than DEFAULT_TYPE
        defaultPostShouldNotBeFound("type.lessThan=" + DEFAULT_TYPE);

        // Get all the postList where type is less than UPDATED_TYPE
        defaultPostShouldBeFound("type.lessThan=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllPostsByTypeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where type is greater than DEFAULT_TYPE
        defaultPostShouldNotBeFound("type.greaterThan=" + DEFAULT_TYPE);

        // Get all the postList where type is greater than SMALLER_TYPE
        defaultPostShouldBeFound("type.greaterThan=" + SMALLER_TYPE);
    }


    @Test
    @Transactional
    public void getAllPostsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        postRepository.saveAndFlush(post);
        Long userId = user.getId();

        // Get all the postList where user equals to userId
        defaultPostShouldBeFound("userId.equals=" + userId);

        // Get all the postList where user equals to userId + 1
        defaultPostShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
            .andExpect(jsonPath("$.[*].like").value(hasItem(DEFAULT_LIKE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));

        // Check, that the count call also returns 1
        restPostMockMvc.perform(get("/api/posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPostMockMvc.perform(get("/api/posts/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .content(UPDATED_CONTENT)
            .createTime(UPDATED_CREATE_TIME)
            .like(UPDATED_LIKE)
            .type(UPDATED_TYPE);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testPost.getCreateTime()).isEqualTo(UPDATED_CREATE_TIME);
        assertThat(testPost.getLike()).isEqualTo(UPDATED_LIKE);
        assertThat(testPost.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc.perform(put("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Delete the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
