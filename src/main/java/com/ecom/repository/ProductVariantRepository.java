package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.ProductVariant;
import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
	List<ProductVariant> findByProductId(Integer productId);
	List<ProductVariant> findByProductIdAndVariantType(Integer productId, String variantType);
	void deleteByProductId(Integer productId);
}
