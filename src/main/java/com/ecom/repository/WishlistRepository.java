package com.ecom.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.ecom.model.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

	List<Wishlist> findByUserId(Integer userId);

	Boolean existsByUserIdAndProductId(Integer userId, Integer productId);

	@Transactional
	void deleteByUserIdAndProductId(Integer userId, Integer productId);
}
