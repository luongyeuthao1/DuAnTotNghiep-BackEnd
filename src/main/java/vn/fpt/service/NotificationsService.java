package vn.fpt.service;

import vn.fpt.domain.Notifications;
import vn.fpt.repository.NotificationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Notifications}.
 */
@Service
@Transactional
public class NotificationsService {

    private final Logger log = LoggerFactory.getLogger(NotificationsService.class);

    private final NotificationsRepository notificationsRepository;

    public NotificationsService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    /**
     * Save a notifications.
     *
     * @param notifications the entity to save.
     * @return the persisted entity.
     */
    public Notifications save(Notifications notifications) {
        log.debug("Request to save Notifications : {}", notifications);
        return notificationsRepository.save(notifications);
    }

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Notifications> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationsRepository.findAll(pageable);
    }


    /**
     * Get one notifications by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Notifications> findOne(Long id) {
        log.debug("Request to get Notifications : {}", id);
        return notificationsRepository.findById(id);
    }

    /**
     * Delete the notifications by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Notifications : {}", id);
        notificationsRepository.deleteById(id);
    }
}
