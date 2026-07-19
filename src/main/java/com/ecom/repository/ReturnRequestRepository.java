package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.ReturnRequest;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {
	List<ReturnRequest> findByUserId(Integer userId);
	List<ReturnRequest> findByStatus(String status);
	ReturnRequest findByOrderId(Integer orderId);
}
