package vn.fpt.repository;

import vn.fpt.domain.Apply;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Apply entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long>, JpaSpecificationExecutor<Apply> {

    @Query("select apply from Apply apply where apply.user.login = ?#{principal.username}")
    List<Apply> findByUserIsCurrentUser();
}
