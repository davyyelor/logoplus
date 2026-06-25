package com.logopeda.notification.repository;

import com.logopeda.notification.model.Notification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    Optional<Notification> findByIdAndUserId(String id, String userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    long countByUserIdAndReadFlagFalse(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.readFlag = true WHERE n.userId = :userId AND n.readFlag = false")
    int markAllRead(@Param("userId") String userId);
}
