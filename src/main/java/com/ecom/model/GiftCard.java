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
public class GiftCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	private String code;

	private Double amount;

	private Boolean isUsed = false;

	private Integer usedByUserId;

	@Temporal(TemporalType.DATE)
	@org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date expiryDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public GiftCard() {
	}

	public GiftCard(Integer id, String code, Double amount, Boolean isUsed, Integer usedByUserId, Date expiryDate, Date createdAt) {
		this.id = id;
		this.code = code;
		this.amount = amount;
		this.isUsed = isUsed;
		this.usedByUserId = usedByUserId;
		this.expiryDate = expiryDate;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Integer getUsedByUserId() {
		return usedByUserId;
	}

	public void setUsedByUserId(Integer usedByUserId) {
		this.usedByUserId = usedByUserId;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
