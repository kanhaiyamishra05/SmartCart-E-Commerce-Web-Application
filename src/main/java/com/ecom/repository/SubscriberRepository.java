package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.Subscriber;
import java.util.List;

public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
	boolean existsByEmail(String email);
	Subscriber findByEmail(String email);
	List<Subscriber> findByIsActiveTrue();
}
