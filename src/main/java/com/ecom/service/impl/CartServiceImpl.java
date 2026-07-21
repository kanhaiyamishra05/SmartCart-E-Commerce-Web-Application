package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		if (productId == null || userId == null) return null;

		UserDtls userDtls = userRepository.findById(userId).orElse(null);
		Product product = productRepository.findById(productId).orElse(null);

		if (userDtls == null || product == null) {
			return null;
		}

		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

		Cart cart = null;
		Double discountPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : (product.getPrice() != null ? product.getPrice() : 0.0);

		if (ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(Math.round((1 * discountPrice) * 100.0) / 100.0);
		} else {
			cart = cartStatus;
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(Math.round((cart.getQuantity() * discountPrice) * 100.0) / 100.0);
		}
		return cartRepository.save(cart);
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		if (userId == null) return new ArrayList<>();

		List<Cart> carts = cartRepository.findByUserId(userId);
		if (carts == null) return new ArrayList<>();

		Double totalOrderPrice = 0.0;
		List<Cart> updateCarts = new ArrayList<>();
		for (Cart c : carts) {
			if (c != null && c.getProduct() != null) {
				Double discountPrice = c.getProduct().getDiscountPrice() != null ? c.getProduct().getDiscountPrice() : (c.getProduct().getPrice() != null ? c.getProduct().getPrice() : 0.0);
				Double totalPrice = Math.round((discountPrice * c.getQuantity()) * 100.0) / 100.0;
				c.setTotalPrice(totalPrice);
				totalOrderPrice = Math.round((totalOrderPrice + totalPrice) * 100.0) / 100.0;
				c.setTotalOrderPrice(totalOrderPrice);
				updateCarts.add(c);
			}
		}

		return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		if (userId == null) return 0;
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId != null ? countByUserId : 0;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		if (cid == null) return;
		Cart cart = cartRepository.findById(cid).orElse(null);
		if (cart == null || cart.getProduct() == null) return;

		int updateQuantity;

		if (sy != null && sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity() - 1;

			if (updateQuantity <= 0) {
				cartRepository.delete(cart);
				return;
			} else {
				cart.setQuantity(updateQuantity);
			}

		} else {
			updateQuantity = cart.getQuantity() + 1;
			cart.setQuantity(updateQuantity);
		}
		Double discountPrice = cart.getProduct().getDiscountPrice() != null ? cart.getProduct().getDiscountPrice() : (cart.getProduct().getPrice() != null ? cart.getProduct().getPrice() : 0.0);
		cart.setTotalPrice(Math.round((updateQuantity * discountPrice) * 100.0) / 100.0);
		cartRepository.save(cart);
	}

}
