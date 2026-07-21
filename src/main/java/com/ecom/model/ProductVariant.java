package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ProductVariant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer productId;

	private String variantType;

	private String variantValue;

	private Double extraPrice = 0.0;

	private Integer stock = 0;

	public ProductVariant() {
	}

	public ProductVariant(Integer id, Integer productId, String variantType, String variantValue, Double extraPrice, Integer stock) {
		this.id = id;
		this.productId = productId;
		this.variantType = variantType;
		this.variantValue = variantValue;
		this.extraPrice = extraPrice;
		this.stock = stock;
	}

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }

	public Integer getProductId() { return productId; }
	public void setProductId(Integer productId) { this.productId = productId; }

	public String getVariantType() { return variantType; }
	public void setVariantType(String variantType) { this.variantType = variantType; }

	public String getVariantValue() { return variantValue; }
	public void setVariantValue(String variantValue) { this.variantValue = variantValue; }

	public Double getExtraPrice() { return extraPrice; }
	public void setExtraPrice(Double extraPrice) { this.extraPrice = extraPrice; }

	public Integer getStock() { return stock; }
	public void setStock(Integer stock) { this.stock = stock; }
}
