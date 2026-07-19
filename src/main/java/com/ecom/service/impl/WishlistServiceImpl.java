package com.ecom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.model.Wishlist;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.repository.WishlistRepository;
import com.ecom.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Wishlist addToWishlist(Integer productId, Integer userId) {
		if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			return null;
		}
		Product product = productRepository.findById(productId).orElse(null);
		UserDtls user = userRepository.findById(userId).orElse(null);
		if (product != null && user != null) {
			Wishlist wishlist = new Wishlist();
			wishlist.setProduct(product);
			wishlist.setUser(user);
			return wishlistRepository.save(wishlist);
		}
		return null;
	}

	@Override
	public List<Wishlist> getWishlistByUser(Integer userId) {
		return wishlistRepository.findByUserId(userId);
	}

	@Override
	public Boolean existsInWishlist(Integer productId, Integer userId) {
		return wishlistRepository.existsByUserIdAndProductId(userId, productId);
	}

	@Override
	public void removeFromWishlist(Integer productId, Integer userId) {
		wishlistRepository.deleteByUserIdAndProductId(userId, productId);
	}
}
