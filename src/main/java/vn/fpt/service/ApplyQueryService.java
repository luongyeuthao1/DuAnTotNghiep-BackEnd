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

import vn.fpt.domain.Apply;
import vn.fpt.domain.*; // for static metamodels
import vn.fpt.repository.ApplyRepository;
import vn.fpt.service.dto.ApplyCriteria;

/**
 * Service for executing complex queries for {@link Apply} entities in the database.
 * The main input is a {@link ApplyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Apply} or a {@link Page} of {@link Apply} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ApplyQueryService extends QueryService<Apply> {

    private final Logger log = LoggerFactory.getLogger(ApplyQueryService.class);

    private final ApplyRepository applyRepository;

    public ApplyQueryService(ApplyRepository applyRepository) {
        this.applyRepository = applyRepository;
    }

    /**
     * Return a {@link List} of {@link Apply} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Apply> findByCriteria(ApplyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Apply> specification = createSpecification(criteria);
        return applyRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Apply} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Apply> findByCriteria(ApplyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Apply> specification = createSpecification(criteria);
        return applyRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ApplyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Apply> specification = createSpecification(criteria);
        return applyRepository.count(specification);
    }

    /**
     * Function to convert {@link ApplyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Apply> createSpecification(ApplyCriteria criteria) {
        Specification<Apply> specification = Specification.where(null);
        if (criteria != null) {
//            if (criteria.getId() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getId(), Apply_.id));
//            }
//            if (criteria.getTime() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getTime(), Apply_.time));
//            }
//            if (criteria.getContent() != null) {
//                specification = specification.and(buildStringSpecification(criteria.getContent(), Apply_.content));
//            }
//            if (criteria.getUserId() != null) {
//                specification = specification.and(buildSpecification(criteria.getUserId(),
//                    root -> root.join(Apply_.user, JoinType.LEFT).get(User_.id)));
//            }
//            if (criteria.getPostId() != null) {
//                specification = specification.and(buildSpecification(criteria.getPostId(),
//                    root -> root.join(Apply_.post, JoinType.LEFT).get(Post_.id)));
//            }
        }
        return specification;
    }
}
