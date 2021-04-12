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

import vn.fpt.domain.Rating;
import vn.fpt.domain.*; // for static metamodels
import vn.fpt.repository.RatingRepository;
import vn.fpt.service.dto.RatingCriteria;

/**
 * Service for executing complex queries for {@link Rating} entities in the database.
 * The main input is a {@link RatingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Rating} or a {@link Page} of {@link Rating} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RatingQueryService extends QueryService<Rating> {

    private final Logger log = LoggerFactory.getLogger(RatingQueryService.class);

    private final RatingRepository ratingRepository;

    public RatingQueryService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /**
     * Return a {@link List} of {@link Rating} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Rating> findByCriteria(RatingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Rating} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Rating> findByCriteria(RatingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RatingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingRepository.count(specification);
    }

    /**
     * Function to convert {@link RatingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Rating> createSpecification(RatingCriteria criteria) {
        Specification<Rating> specification = Specification.where(null);
        if (criteria != null) {
//            if (criteria.getId() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getId(), Rating_.id));
//            }
//            if (criteria.getContent() != null) {
//                specification = specification.and(buildStringSpecification(criteria.getContent(), Rating_.content));
//            }
//            if (criteria.getTimes() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getTimes(), Rating_.times));
//            }
//            if (criteria.getRank() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getRank(), Rating_.rank));
//            }
//            if (criteria.getUserId() != null) {
//                specification = specification.and(buildSpecification(criteria.getUserId(),
//                    root -> root.join(Rating_.user, JoinType.LEFT).get(User_.id)));
//            }
        }
        return specification;
    }
}
