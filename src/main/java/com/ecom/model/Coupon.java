package com.ecom.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	private String code;

	private Double discountPercentage;

	private Double minOrderAmount;

	@Temporal(TemporalType.DATE)
	@org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date expiryDate;

	private Boolean isActive;

	private Integer usageCount = 0;

	public Coupon() {
	}

	public Coupon(Integer id, String code, Double discountPercentage, Double minOrderAmount, Date expiryDate, Boolean isActive, Integer usageCount) {
		this.id = id;
		this.code = code;
		this.discountPercentage = discountPercentage;
		this.minOrderAmount = minOrderAmount;
		this.expiryDate = expiryDate;
		this.isActive = isActive;
		this.usageCount = usageCount;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	public Double getDiscountPercentage() { return discountPercentage; }
	public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

	public Double getMinOrderAmount() { return minOrderAmount; }
	public void setMinOrderAmount(Double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

	public Date getExpiryDate() { return expiryDate; }
	public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean isActive) { this.isActive = isActive; }

	public Integer getUsageCount() { return usageCount; }
	public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
}
