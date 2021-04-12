package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Images;
import vn.fpt.domain.Post;
import vn.fpt.repository.ImagesRepository;
import vn.fpt.service.ImagesService;
import vn.fpt.service.dto.ImagesCriteria;
import vn.fpt.service.ImagesQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ImagesResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class ImagesResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private ImagesQueryService imagesQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restImagesMockMvc;

    private Images images;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Images createEntity(EntityManager em) {
        Images images = new Images()
            .url(DEFAULT_URL);
        return images;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Images createUpdatedEntity(EntityManager em) {
        Images images = new Images()
            .url(UPDATED_URL);
        return images;
    }

    @BeforeEach
    public void initTest() {
        images = createEntity(em);
    }

    @Test
    @Transactional
    public void createImages() throws Exception {
        int databaseSizeBeforeCreate = imagesRepository.findAll().size();
        // Create the Images
        restImagesMockMvc.perform(post("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isCreated());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeCreate + 1);
        Images testImages = imagesList.get(imagesList.size() - 1);
        assertThat(testImages.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    public void createImagesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imagesRepository.findAll().size();

        // Create the Images with an existing ID
        images.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImagesMockMvc.perform(post("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllImages() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList
        restImagesMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(images.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));
    }
    
    @Test
    @Transactional
    public void getImages() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get the images
        restImagesMockMvc.perform(get("/api/images/{id}", images.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(images.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL));
    }


    @Test
    @Transactional
    public void getImagesByIdFiltering() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        Long id = images.getId();

        defaultImagesShouldBeFound("id.equals=" + id);
        defaultImagesShouldNotBeFound("id.notEquals=" + id);

        defaultImagesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultImagesShouldNotBeFound("id.greaterThan=" + id);

        defaultImagesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultImagesShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllImagesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url equals to DEFAULT_URL
        defaultImagesShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the imagesList where url equals to UPDATED_URL
        defaultImagesShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllImagesByUrlIsNotEqualToSomething() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url not equals to DEFAULT_URL
        defaultImagesShouldNotBeFound("url.notEquals=" + DEFAULT_URL);

        // Get all the imagesList where url not equals to UPDATED_URL
        defaultImagesShouldBeFound("url.notEquals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllImagesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url in DEFAULT_URL or UPDATED_URL
        defaultImagesShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the imagesList where url equals to UPDATED_URL
        defaultImagesShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllImagesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url is not null
        defaultImagesShouldBeFound("url.specified=true");

        // Get all the imagesList where url is null
        defaultImagesShouldNotBeFound("url.specified=false");
    }
                @Test
    @Transactional
    public void getAllImagesByUrlContainsSomething() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url contains DEFAULT_URL
        defaultImagesShouldBeFound("url.contains=" + DEFAULT_URL);

        // Get all the imagesList where url contains UPDATED_URL
        defaultImagesShouldNotBeFound("url.contains=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllImagesByUrlNotContainsSomething() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);

        // Get all the imagesList where url does not contain DEFAULT_URL
        defaultImagesShouldNotBeFound("url.doesNotContain=" + DEFAULT_URL);

        // Get all the imagesList where url does not contain UPDATED_URL
        defaultImagesShouldBeFound("url.doesNotContain=" + UPDATED_URL);
    }


    @Test
    @Transactional
    public void getAllImagesByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        imagesRepository.saveAndFlush(images);
        Post post = PostResourceIT.createEntity(em);
        em.persist(post);
        em.flush();
        images.setPost(post);
        imagesRepository.saveAndFlush(images);
        Long postId = post.getId();

        // Get all the imagesList where post equals to postId
        defaultImagesShouldBeFound("postId.equals=" + postId);

        // Get all the imagesList where post equals to postId + 1
        defaultImagesShouldNotBeFound("postId.equals=" + (postId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultImagesShouldBeFound(String filter) throws Exception {
        restImagesMockMvc.perform(get("/api/images?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(images.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)));

        // Check, that the count call also returns 1
        restImagesMockMvc.perform(get("/api/images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultImagesShouldNotBeFound(String filter) throws Exception {
        restImagesMockMvc.perform(get("/api/images?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restImagesMockMvc.perform(get("/api/images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingImages() throws Exception {
        // Get the images
        restImagesMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImages() throws Exception {
        // Initialize the database
        imagesService.save(images);

        int databaseSizeBeforeUpdate = imagesRepository.findAll().size();

        // Update the images
        Images updatedImages = imagesRepository.findById(images.getId()).get();
        // Disconnect from session so that the updates on updatedImages are not directly saved in db
        em.detach(updatedImages);
        updatedImages
            .url(UPDATED_URL);

        restImagesMockMvc.perform(put("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedImages)))
            .andExpect(status().isOk());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeUpdate);
        Images testImages = imagesList.get(imagesList.size() - 1);
        assertThat(testImages.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingImages() throws Exception {
        int databaseSizeBeforeUpdate = imagesRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImagesMockMvc.perform(put("/api/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteImages() throws Exception {
        // Initialize the database
        imagesService.save(images);

        int databaseSizeBeforeDelete = imagesRepository.findAll().size();

        // Delete the images
        restImagesMockMvc.perform(delete("/api/images/{id}", images.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
