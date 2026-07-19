package com.ecom.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

	// Track how many times this coupon has been used
	private Integer usageCount = 0;
}
