package com.ecom.model;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class ProductReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private UserDtls user;

	@ManyToOne
	private Product product;

	private Integer rating;

	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reviewDate;

	private Boolean isApproved = true;

	public ProductReview() {
	}

	public ProductReview(Integer id, UserDtls user, Product product, Integer rating, String comment, Date reviewDate, Boolean isApproved) {
		this.id = id;
		this.user = user;
		this.product = product;
		this.rating = rating;
		this.comment = comment;
		this.reviewDate = reviewDate;
		this.isApproved = isApproved;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public UserDtls getUser() { return user; }
	public void setUser(UserDtls user) { this.user = user; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }

	public Integer getRating() { return rating; }
	public void setRating(Integer rating) { this.rating = rating; }

	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }

	public Date getReviewDate() { return reviewDate; }
	public void setReviewDate(Date reviewDate) { this.reviewDate = reviewDate; }

	public Boolean getIsApproved() { return isApproved; }
	public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
}
