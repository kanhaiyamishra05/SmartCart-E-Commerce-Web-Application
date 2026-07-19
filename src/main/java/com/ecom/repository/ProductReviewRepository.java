package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

	List<ProductReview> findByProductIdOrderByReviewDateDesc(Integer productId);

	List<ProductReview> findByProductId(Integer productId);

	// Only approved reviews for public display
	List<ProductReview> findByProductIdAndIsApprovedTrueOrderByReviewDateDesc(Integer productId);

	// All reviews for admin moderation
	List<ProductReview> findByIsApprovedFalse();

	List<ProductReview> findAllByOrderByReviewDateDesc();
}
