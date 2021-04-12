package vn.fpt.web.rest;

import vn.fpt.DuAnTotNghiepBackEndApp;
import vn.fpt.domain.Notifications;
import vn.fpt.domain.User;
import vn.fpt.repository.NotificationsRepository;
import vn.fpt.service.NotificationsService;
import vn.fpt.service.dto.NotificationsCriteria;
import vn.fpt.service.NotificationsQueryService;

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
 * Integration tests for the {@link NotificationsResource} REST controller.
 */
@SpringBootTest(classes = DuAnTotNghiepBackEndApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class NotificationsResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_TIMES = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_TIMES = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_TIMES = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private NotificationsQueryService notificationsQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationsMockMvc;

    private Notifications notifications;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notifications createEntity(EntityManager em) {
        Notifications notifications = new Notifications()
            .content(DEFAULT_CONTENT)
            .times(DEFAULT_TIMES)
            .status(DEFAULT_STATUS);
        return notifications;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notifications createUpdatedEntity(EntityManager em) {
        Notifications notifications = new Notifications()
            .content(UPDATED_CONTENT)
            .times(UPDATED_TIMES)
            .status(UPDATED_STATUS);
        return notifications;
    }

    @BeforeEach
    public void initTest() {
        notifications = createEntity(em);
    }

    @Test
    @Transactional
    public void createNotifications() throws Exception {
        int databaseSizeBeforeCreate = notificationsRepository.findAll().size();
        // Create the Notifications
        restNotificationsMockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notifications)))
            .andExpect(status().isCreated());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeCreate + 1);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotifications.getTimes()).isEqualTo(DEFAULT_TIMES);
        assertThat(testNotifications.isStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createNotificationsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = notificationsRepository.findAll().size();

        // Create the Notifications with an existing ID
        notifications.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationsMockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notifications)))
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList
        restNotificationsMockMvc.perform(get("/api/notifications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].times").value(hasItem(sameInstant(DEFAULT_TIMES))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getNotifications() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get the notifications
        restNotificationsMockMvc.perform(get("/api/notifications/{id}", notifications.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notifications.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.times").value(sameInstant(DEFAULT_TIMES)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }


    @Test
    @Transactional
    public void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        Long id = notifications.getId();

        defaultNotificationsShouldBeFound("id.equals=" + id);
        defaultNotificationsShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationsShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationsShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllNotificationsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content equals to DEFAULT_CONTENT
        defaultNotificationsShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the notificationsList where content equals to UPDATED_CONTENT
        defaultNotificationsShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNotificationsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content not equals to DEFAULT_CONTENT
        defaultNotificationsShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the notificationsList where content not equals to UPDATED_CONTENT
        defaultNotificationsShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNotificationsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultNotificationsShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the notificationsList where content equals to UPDATED_CONTENT
        defaultNotificationsShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNotificationsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content is not null
        defaultNotificationsShouldBeFound("content.specified=true");

        // Get all the notificationsList where content is null
        defaultNotificationsShouldNotBeFound("content.specified=false");
    }
                @Test
    @Transactional
    public void getAllNotificationsByContentContainsSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content contains DEFAULT_CONTENT
        defaultNotificationsShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the notificationsList where content contains UPDATED_CONTENT
        defaultNotificationsShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNotificationsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where content does not contain DEFAULT_CONTENT
        defaultNotificationsShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the notificationsList where content does not contain UPDATED_CONTENT
        defaultNotificationsShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }


    @Test
    @Transactional
    public void getAllNotificationsByTimesIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times equals to DEFAULT_TIMES
        defaultNotificationsShouldBeFound("times.equals=" + DEFAULT_TIMES);

        // Get all the notificationsList where times equals to UPDATED_TIMES
        defaultNotificationsShouldNotBeFound("times.equals=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times not equals to DEFAULT_TIMES
        defaultNotificationsShouldNotBeFound("times.notEquals=" + DEFAULT_TIMES);

        // Get all the notificationsList where times not equals to UPDATED_TIMES
        defaultNotificationsShouldBeFound("times.notEquals=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times in DEFAULT_TIMES or UPDATED_TIMES
        defaultNotificationsShouldBeFound("times.in=" + DEFAULT_TIMES + "," + UPDATED_TIMES);

        // Get all the notificationsList where times equals to UPDATED_TIMES
        defaultNotificationsShouldNotBeFound("times.in=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times is not null
        defaultNotificationsShouldBeFound("times.specified=true");

        // Get all the notificationsList where times is null
        defaultNotificationsShouldNotBeFound("times.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times is greater than or equal to DEFAULT_TIMES
        defaultNotificationsShouldBeFound("times.greaterThanOrEqual=" + DEFAULT_TIMES);

        // Get all the notificationsList where times is greater than or equal to UPDATED_TIMES
        defaultNotificationsShouldNotBeFound("times.greaterThanOrEqual=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times is less than or equal to DEFAULT_TIMES
        defaultNotificationsShouldBeFound("times.lessThanOrEqual=" + DEFAULT_TIMES);

        // Get all the notificationsList where times is less than or equal to SMALLER_TIMES
        defaultNotificationsShouldNotBeFound("times.lessThanOrEqual=" + SMALLER_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsLessThanSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times is less than DEFAULT_TIMES
        defaultNotificationsShouldNotBeFound("times.lessThan=" + DEFAULT_TIMES);

        // Get all the notificationsList where times is less than UPDATED_TIMES
        defaultNotificationsShouldBeFound("times.lessThan=" + UPDATED_TIMES);
    }

    @Test
    @Transactional
    public void getAllNotificationsByTimesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where times is greater than DEFAULT_TIMES
        defaultNotificationsShouldNotBeFound("times.greaterThan=" + DEFAULT_TIMES);

        // Get all the notificationsList where times is greater than SMALLER_TIMES
        defaultNotificationsShouldBeFound("times.greaterThan=" + SMALLER_TIMES);
    }


    @Test
    @Transactional
    public void getAllNotificationsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status equals to DEFAULT_STATUS
        defaultNotificationsShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the notificationsList where status equals to UPDATED_STATUS
        defaultNotificationsShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status not equals to DEFAULT_STATUS
        defaultNotificationsShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the notificationsList where status not equals to UPDATED_STATUS
        defaultNotificationsShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultNotificationsShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the notificationsList where status equals to UPDATED_STATUS
        defaultNotificationsShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllNotificationsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);

        // Get all the notificationsList where status is not null
        defaultNotificationsShouldBeFound("status.specified=true");

        // Get all the notificationsList where status is null
        defaultNotificationsShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllNotificationsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationsRepository.saveAndFlush(notifications);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        notifications.setUser(user);
        notificationsRepository.saveAndFlush(notifications);
        Long userId = user.getId();

        // Get all the notificationsList where user equals to userId
        defaultNotificationsShouldBeFound("userId.equals=" + userId);

        // Get all the notificationsList where user equals to userId + 1
        defaultNotificationsShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationsShouldBeFound(String filter) throws Exception {
        restNotificationsMockMvc.perform(get("/api/notifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notifications.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].times").value(hasItem(sameInstant(DEFAULT_TIMES))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));

        // Check, that the count call also returns 1
        restNotificationsMockMvc.perform(get("/api/notifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationsShouldNotBeFound(String filter) throws Exception {
        restNotificationsMockMvc.perform(get("/api/notifications?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationsMockMvc.perform(get("/api/notifications/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingNotifications() throws Exception {
        // Get the notifications
        restNotificationsMockMvc.perform(get("/api/notifications/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNotifications() throws Exception {
        // Initialize the database
        notificationsService.save(notifications);

        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();

        // Update the notifications
        Notifications updatedNotifications = notificationsRepository.findById(notifications.getId()).get();
        // Disconnect from session so that the updates on updatedNotifications are not directly saved in db
        em.detach(updatedNotifications);
        updatedNotifications
            .content(UPDATED_CONTENT)
            .times(UPDATED_TIMES)
            .status(UPDATED_STATUS);

        restNotificationsMockMvc.perform(put("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedNotifications)))
            .andExpect(status().isOk());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
        Notifications testNotifications = notificationsList.get(notificationsList.size() - 1);
        assertThat(testNotifications.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testNotifications.getTimes()).isEqualTo(UPDATED_TIMES);
        assertThat(testNotifications.isStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingNotifications() throws Exception {
        int databaseSizeBeforeUpdate = notificationsRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationsMockMvc.perform(put("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(notifications)))
            .andExpect(status().isBadRequest());

        // Validate the Notifications in the database
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNotifications() throws Exception {
        // Initialize the database
        notificationsService.save(notifications);

        int databaseSizeBeforeDelete = notificationsRepository.findAll().size();

        // Delete the notifications
        restNotificationsMockMvc.perform(delete("/api/notifications/{id}", notifications.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Notifications> notificationsList = notificationsRepository.findAll();
        assertThat(notificationsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
