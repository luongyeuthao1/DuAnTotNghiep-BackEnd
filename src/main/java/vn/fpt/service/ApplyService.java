package vn.fpt.service;

import vn.fpt.domain.Apply;
import vn.fpt.repository.ApplyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Apply}.
 */
@Service
@Transactional
public class ApplyService {

    private final Logger log = LoggerFactory.getLogger(ApplyService.class);

    private final ApplyRepository applyRepository;

    public ApplyService(ApplyRepository applyRepository) {
        this.applyRepository = applyRepository;
    }

    /**
     * Save a apply.
     *
     * @param apply the entity to save.
     * @return the persisted entity.
     */
    public Apply save(Apply apply) {
        log.debug("Request to save Apply : {}", apply);
        return applyRepository.save(apply);
    }

    /**
     * Get all the applies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Apply> findAll(Pageable pageable) {
        log.debug("Request to get all Applies");
        return applyRepository.findAll(pageable);
    }


    /**
     * Get one apply by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Apply> findOne(Long id) {
        log.debug("Request to get Apply : {}", id);
        return applyRepository.findById(id);
    }

    /**
     * Delete the apply by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Apply : {}", id);
        applyRepository.deleteById(id);
    }
}
