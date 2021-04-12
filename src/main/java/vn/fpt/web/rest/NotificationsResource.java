package vn.fpt.web.rest;

import vn.fpt.domain.Notifications;
import vn.fpt.service.NotificationsService;
import vn.fpt.web.rest.errors.BadRequestAlertException;
import vn.fpt.service.dto.NotificationsCriteria;
import vn.fpt.service.NotificationsQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.fpt.domain.Notifications}.
 */
@RestController
@RequestMapping("/api")
public class NotificationsResource {

    private final Logger log = LoggerFactory.getLogger(NotificationsResource.class);

    private static final String ENTITY_NAME = "notifications";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationsService notificationsService;

    private final NotificationsQueryService notificationsQueryService;

    public NotificationsResource(NotificationsService notificationsService, NotificationsQueryService notificationsQueryService) {
        this.notificationsService = notificationsService;
        this.notificationsQueryService = notificationsQueryService;
    }

    /**
     * {@code POST  /notifications} : Create a new notifications.
     *
     * @param notifications the notifications to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notifications, or with status {@code 400 (Bad Request)} if the notifications has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notifications")
    public ResponseEntity<Notifications> createNotifications(@RequestBody Notifications notifications) throws URISyntaxException {
        log.debug("REST request to save Notifications : {}", notifications);
        if (notifications.getId() != null) {
            throw new BadRequestAlertException("A new notifications cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Notifications result = notificationsService.save(notifications);
        return ResponseEntity.created(new URI("/api/notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notifications} : Updates an existing notifications.
     *
     * @param notifications the notifications to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notifications,
     * or with status {@code 400 (Bad Request)} if the notifications is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notifications couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications")
    public ResponseEntity<Notifications> updateNotifications(@RequestBody Notifications notifications) throws URISyntaxException {
        log.debug("REST request to update Notifications : {}", notifications);
        if (notifications.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Notifications result = notificationsService.save(notifications);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notifications.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<Notifications>> getAllNotifications(NotificationsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Notifications by criteria: {}", criteria);
        Page<Notifications> page = notificationsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/count} : count all the notifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/notifications/count")
    public ResponseEntity<Long> countNotifications(NotificationsCriteria criteria) {
        log.debug("REST request to count Notifications by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notifications.
     *
     * @param id the id of the notifications to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notifications, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notifications/{id}")
    public ResponseEntity<Notifications> getNotifications(@PathVariable Long id) {
        log.debug("REST request to get Notifications : {}", id);
        Optional<Notifications> notifications = notificationsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notifications);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notifications.
     *
     * @param id the id of the notifications to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotifications(@PathVariable Long id) {
        log.debug("REST request to delete Notifications : {}", id);
        notificationsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
