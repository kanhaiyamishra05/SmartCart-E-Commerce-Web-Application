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
}
