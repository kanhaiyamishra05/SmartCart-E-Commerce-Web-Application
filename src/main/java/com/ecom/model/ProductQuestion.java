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
public class ProductQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private Product product;

	@ManyToOne
	private UserDtls askedBy;

	private String question;

	private String answer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date askedAt;

	public ProductQuestion() {
	}

	public ProductQuestion(Integer id, Product product, UserDtls askedBy, String question, String answer, Date askedAt) {
		this.id = id;
		this.product = product;
		this.askedBy = askedBy;
		this.question = question;
		this.answer = answer;
		this.askedAt = askedAt;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Product getProduct() { return product; }
	public void setProduct(Product product) { this.product = product; }

	public UserDtls getAskedBy() { return askedBy; }
	public void setAskedBy(UserDtls askedBy) { this.askedBy = askedBy; }

	public String getQuestion() { return question; }
	public void setQuestion(String question) { this.question = question; }

	public String getAnswer() { return answer; }
	public void setAnswer(String answer) { this.answer = answer; }

	public Date getAskedAt() { return askedAt; }
	public void setAskedAt(Date askedAt) { this.askedAt = askedAt; }
}
