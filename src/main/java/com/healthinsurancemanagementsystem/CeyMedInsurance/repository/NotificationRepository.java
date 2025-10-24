package com.healthinsurancemanagementsystem.CeyMedInsurance.repository;

import com.healthinsurancemanagementsystem.CeyMedInsurance.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);

    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedDateDesc(Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false")
    Long countUnreadNotificationsByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdDate DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
}
