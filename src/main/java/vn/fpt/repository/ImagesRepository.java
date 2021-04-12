package vn.fpt.repository;

import vn.fpt.domain.Images;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Images entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImagesRepository extends JpaRepository<Images, Long>, JpaSpecificationExecutor<Images> {
}
