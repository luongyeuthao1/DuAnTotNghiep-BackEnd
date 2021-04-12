package vn.fpt.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import vn.fpt.domain.Notifications;
import vn.fpt.domain.*; // for static metamodels
import vn.fpt.repository.NotificationsRepository;
import vn.fpt.service.dto.NotificationsCriteria;

/**
 * Service for executing complex queries for {@link Notifications} entities in the database.
 * The main input is a {@link NotificationsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Notifications} or a {@link Page} of {@link Notifications} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationsQueryService extends QueryService<Notifications> {

    private final Logger log = LoggerFactory.getLogger(NotificationsQueryService.class);

    private final NotificationsRepository notificationsRepository;

    public NotificationsQueryService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    /**
     * Return a {@link List} of {@link Notifications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Notifications> findByCriteria(NotificationsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Notifications} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Notifications> findByCriteria(NotificationsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Notifications> specification = createSpecification(criteria);
        return notificationsRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Notifications> createSpecification(NotificationsCriteria criteria) {
        Specification<Notifications> specification = Specification.where(null);
        if (criteria != null) {
//            if (criteria.getId() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getId(), Notifications_.id));
//            }
//            if (criteria.getContent() != null) {
//                specification = specification.and(buildStringSpecification(criteria.getContent(), Notifications_.content));
//            }
//            if (criteria.getTimes() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getTimes(), Notifications_.times));
//            }
//            if (criteria.getStatus() != null) {
//                specification = specification.and(buildSpecification(criteria.getStatus(), Notifications_.status));
//            }
//            if (criteria.getUserId() != null) {
//                specification = specification.and(buildSpecification(criteria.getUserId(),
//                    root -> root.join(Notifications_.user, JoinType.LEFT).get(User_.id)));
//            }
        }
        return specification;
    }
}
