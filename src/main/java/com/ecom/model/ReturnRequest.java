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
public class ReturnRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private ProductOrder order;

	@ManyToOne
	private UserDtls user;

	private String reason;

	private String status = "PENDING";

	@Temporal(TemporalType.TIMESTAMP)
	private Date requestDate;

	private String adminNote;

	public ReturnRequest() {
	}

	public ReturnRequest(Integer id, ProductOrder order, UserDtls user, String reason, String status, Date requestDate, String adminNote) {
		this.id = id;
		this.order = order;
		this.user = user;
		this.reason = reason;
		this.status = status;
		this.requestDate = requestDate;
		this.adminNote = adminNote;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public ProductOrder getOrder() { return order; }
	public void setOrder(ProductOrder order) { this.order = order; }

	public UserDtls getUser() { return user; }
	public void setUser(UserDtls user) { this.user = user; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public Date getRequestDate() { return requestDate; }
	public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

	public String getAdminNote() { return adminNote; }
	public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
}
