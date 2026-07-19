package com.ecom.service;

import java.util.List;
import com.ecom.model.ProductReview;

public interface ReviewService {

	public ProductReview saveReview(ProductReview review);

	public List<ProductReview> getReviewsByProduct(Integer productId);

	public Double getAverageRating(Integer productId);
}
