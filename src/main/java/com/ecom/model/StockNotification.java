package com.ecom.model;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class StockNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer productId;

	private String userEmail;

	private Boolean isNotified = false;

	@Temporal(TemporalType.TIMESTAMP)
	private Date requestedAt;

	public StockNotification() {
	}

	public StockNotification(Integer id, Integer productId, String userEmail, Boolean isNotified, Date requestedAt) {
		this.id = id;
		this.productId = productId;
		this.userEmail = userEmail;
		this.isNotified = isNotified;
		this.requestedAt = requestedAt;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Integer getProductId() { return productId; }
	public void setProductId(Integer productId) { this.productId = productId; }

	public String getUserEmail() { return userEmail; }
	public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

	public Boolean getIsNotified() { return isNotified; }
	public void setIsNotified(Boolean isNotified) { this.isNotified = isNotified; }

	public Date getRequestedAt() { return requestedAt; }
	public void setRequestedAt(Date requestedAt) { this.requestedAt = requestedAt; }
}
