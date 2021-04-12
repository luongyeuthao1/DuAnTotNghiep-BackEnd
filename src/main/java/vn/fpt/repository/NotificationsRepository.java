package vn.fpt.repository;

import vn.fpt.domain.Notifications;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Notifications entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long>, JpaSpecificationExecutor<Notifications> {

    @Query("select notifications from Notifications notifications where notifications.user.login = ?#{principal.username}")
    List<Notifications> findByUserIsCurrentUser();
}
