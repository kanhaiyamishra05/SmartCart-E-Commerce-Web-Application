package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.StockNotification;

public interface StockNotificationRepository extends JpaRepository<StockNotification, Integer> {

	List<StockNotification> findByProductIdAndIsNotifiedFalse(Integer productId);

	Boolean existsByProductIdAndUserEmailAndIsNotifiedFalse(Integer productId, String userEmail);
}
