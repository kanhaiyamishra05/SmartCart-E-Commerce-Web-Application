package com.ecom.service;

import java.util.List;
import com.ecom.model.Wishlist;

public interface WishlistService {

	public Wishlist addToWishlist(Integer productId, Integer userId);

	public List<Wishlist> getWishlistByUser(Integer userId);

	public Boolean existsInWishlist(Integer productId, Integer userId);

	public void removeFromWishlist(Integer productId, Integer userId);
}
