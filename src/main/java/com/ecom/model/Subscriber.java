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
public class Subscriber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	private String email;

	@Temporal(TemporalType.TIMESTAMP)
	private Date subscribedAt;

	private Boolean isActive = true;

	public Subscriber() {
	}

	public Subscriber(Integer id, String email, Date subscribedAt, Boolean isActive) {
		this.id = id;
		this.email = email;
		this.subscribedAt = subscribedAt;
		this.isActive = isActive;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public Date getSubscribedAt() { return subscribedAt; }
	public void setSubscribedAt(Date subscribedAt) { this.subscribedAt = subscribedAt; }

	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
