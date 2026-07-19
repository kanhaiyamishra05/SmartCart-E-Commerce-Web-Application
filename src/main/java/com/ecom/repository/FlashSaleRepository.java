package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.FlashSale;
import java.util.Date;
import java.util.List;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Integer> {
	List<FlashSale> findByIsActiveTrue();
	List<FlashSale> findByIsActiveTrueAndEndTimeAfter(Date now);
}
