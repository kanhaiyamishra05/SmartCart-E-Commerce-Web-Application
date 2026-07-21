package com.ecom.model;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class FlashSale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer productId;

	private String title;

	private Double discountPercentage;

	@Temporal(TemporalType.TIMESTAMP)
	@org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date endTime;

	private Boolean isActive;

	public FlashSale() {
	}

	public FlashSale(Integer id, Integer productId, String title, Double discountPercentage, Date startTime, Date endTime, Boolean isActive) {
		this.id = id;
		this.productId = productId;
		this.title = title;
		this.discountPercentage = discountPercentage;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isActive = isActive;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Integer getProductId() { return productId; }
	public void setProductId(Integer productId) { this.productId = productId; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public Double getDiscountPercentage() { return discountPercentage; }
	public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

	public Date getStartTime() { return startTime; }
	public void setStartTime(Date startTime) { this.startTime = startTime; }

	public Date getEndTime() { return endTime; }
	public void setEndTime(Date endTime) { this.endTime = endTime; }

	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
