package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Wishlist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private UserDtls user;

	@ManyToOne
	private Product product;

	public Wishlist() {
	}

	public Wishlist(Integer id, UserDtls user, Product product) {
		this.id = id;
		this.user = user;
		this.product = product;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public UserDtls getUser() { return user; }
	public void setUser(UserDtls user) { this.user = user; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }
}
