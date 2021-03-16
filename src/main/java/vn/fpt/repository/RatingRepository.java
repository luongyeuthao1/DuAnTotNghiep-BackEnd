package vn.fpt.repository;

import vn.fpt.domain.Rating;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Rating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long>, JpaSpecificationExecutor<Rating> {

    @Query("select rating from Rating rating where rating.user.login = ?#{principal.username}")
    List<Rating> findByUserIsCurrentUser();
}
