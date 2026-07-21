package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecom.model.ProductQuestion;

public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Integer> {

	List<ProductQuestion> findByProductIdOrderByAskedAtDesc(Integer productId);
}
