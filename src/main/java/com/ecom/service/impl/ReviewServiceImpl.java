package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecom.model.ProductReview;
import com.ecom.repository.ProductReviewRepository;
import com.ecom.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ProductReviewRepository reviewRepository;

	@Override
	public ProductReview saveReview(ProductReview review) {
		review.setReviewDate(new Date());
		return reviewRepository.save(review);
	}

	@Override
	public List<ProductReview> getReviewsByProduct(Integer productId) {
		return reviewRepository.findByProductIdOrderByReviewDateDesc(productId);
	}

	@Override
	public Double getAverageRating(Integer productId) {
		List<ProductReview> reviews = reviewRepository.findByProductId(productId);
		if (reviews.isEmpty()) {
			return 0.0;
		}
		double sum = 0;
		for (ProductReview r : reviews) {
			sum += r.getRating();
		}
		// return average rounded to 1 decimal place
		return Math.round((sum / reviews.size()) * 10.0) / 10.0;
	}
}
