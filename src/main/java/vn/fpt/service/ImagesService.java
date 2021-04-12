package vn.fpt.service;

import vn.fpt.domain.Images;
import vn.fpt.repository.ImagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Images}.
 */
@Service
@Transactional
public class ImagesService {

    private final Logger log = LoggerFactory.getLogger(ImagesService.class);

    private final ImagesRepository imagesRepository;

    public ImagesService(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    /**
     * Save a images.
     *
     * @param images the entity to save.
     * @return the persisted entity.
     */
    public Images save(Images images) {
        log.debug("Request to save Images : {}", images);
        return imagesRepository.save(images);
    }

    /**
     * Get all the images.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Images> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        return imagesRepository.findAll(pageable);
    }


    /**
     * Get one images by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Images> findOne(Long id) {
        log.debug("Request to get Images : {}", id);
        return imagesRepository.findById(id);
    }

    /**
     * Delete the images by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Images : {}", id);
        imagesRepository.deleteById(id);
    }
}
