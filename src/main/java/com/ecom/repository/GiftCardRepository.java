package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.GiftCard;

public interface GiftCardRepository extends JpaRepository<GiftCard, Integer> {
	GiftCard findByCode(String code);
	GiftCard findByCodeAndIsUsedFalse(String code);
}
