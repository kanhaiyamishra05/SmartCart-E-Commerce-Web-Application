package com.ecom.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.model.Category;
import com.ecom.model.Coupon;
import com.ecom.model.FlashSale;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.repository.FlashSaleRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.CouponService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Centralized REST API Controller for SmartCart.
 * Consolidates store catalog, orders, coupons, flash sales, and analytics into unified JSON endpoints.
 */
@Tag(name = "Centralized Store REST APIs", description = "Endpoints for Catalog, Analytics, and Overview Data")
@RestController
@RequestMapping("/api/v1")
public class ApiController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CouponService couponService;

	@Autowired
	private FlashSaleRepository flashSaleRepository;

	@Autowired
	private ProductOrderRepository productOrderRepository;

	@Autowired
	private UserService userService;

	/**
	 * Centralized Store Overview API Endpoint
	 */
	@Operation(summary = "Get Consolidated Store Overview Payload", description = "Returns active products, categories, active coupons, and live flash sales in a single JSON payload.")
	@GetMapping("/store-overview")
	public ResponseEntity<Map<String, Object>> getStoreOverview() {
		Map<String, Object> data = new HashMap<>();
		try {
			List<Category> categories = categoryService.getAllActiveCategory();
			List<Product> products = productService.getAllActiveProducts("");
			List<Coupon> activeCoupons = couponService.getActiveCoupons();
			List<FlashSale> activeFlashSales = flashSaleRepository.findByIsActiveTrueAndEndTimeAfter(new java.util.Date());

			data.put("status", "SUCCESS");
			data.put("totalCategories", categories != null ? categories.size() : 0);
			data.put("totalActiveProducts", products != null ? products.size() : 0);
			data.put("categories", categories);
			data.put("activeCouponsCount", activeCoupons != null ? activeCoupons.size() : 0);
			data.put("activeCoupons", activeCoupons);
			data.put("activeFlashSalesCount", activeFlashSales != null ? activeFlashSales.size() : 0);
			data.put("activeFlashSales", activeFlashSales);
			data.put("timestamp", new java.util.Date());

			return ResponseEntity.ok(data);
		} catch (Exception e) {
			data.put("status", "ERROR");
			data.put("message", e.getMessage());
			return ResponseEntity.ok(data);
		}
	}

	/**
	 * Centralized Product Search & Filter API
	 */
	@Operation(summary = "Search & Filter Catalog Products", description = "Returns filtered product list based on keyword search and category.")
	@GetMapping("/products")
	public ResponseEntity<Map<String, Object>> searchProducts(
			@RequestParam(defaultValue = "") String ch,
			@RequestParam(defaultValue = "") String category) {
		Map<String, Object> response = new HashMap<>();
		try {
			List<Product> products = productService.getAllActiveProducts(category);
			response.put("status", "SUCCESS");
			response.put("query", ch);
			response.put("category", category);
			response.put("count", products != null ? products.size() : 0);
			response.put("products", products);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("status", "ERROR");
			response.put("message", e.getMessage());
			return ResponseEntity.ok(response);
		}
	}

	/**
	 * Centralized Analytics Endpoint (For Admin & External Dashboards)
	 */
	@Operation(summary = "Get Store Revenue & Analytics Metrics", description = "Returns total revenue (INR), total orders, registered users, and order status breakdown.")
	@GetMapping("/analytics")
	public ResponseEntity<Map<String, Object>> getAnalytics() {
		Map<String, Object> analytics = new HashMap<>();
		try {
			List<ProductOrder> allOrders = productOrderRepository.findAll();
			double totalRevenue = (allOrders != null) ? allOrders.stream()
					.filter(o -> o != null && !"Cancelled".equalsIgnoreCase(o.getStatus()))
					.mapToDouble(o -> (o.getPrice() != null ? o.getPrice() : 0.0) * (o.getQuantity() != null ? o.getQuantity() : 1))
					.sum() : 0.0;

			analytics.put("status", "SUCCESS");
			analytics.put("totalOrders", allOrders != null ? allOrders.size() : 0);
			analytics.put("totalRevenueINR", totalRevenue);
			analytics.put("totalProducts", productService.getAllProducts() != null ? productService.getAllProducts().size() : 0);
			analytics.put("registeredUsers", userService.getUsers("ROLE_USER") != null ? userService.getUsers("ROLE_USER").size() : 0);
			analytics.put("pendingOrders", allOrders != null ? allOrders.stream().filter(o -> o != null && "In Progress".equalsIgnoreCase(o.getStatus())).count() : 0);
			analytics.put("deliveredOrders", allOrders != null ? allOrders.stream().filter(o -> o != null && "Delivered".equalsIgnoreCase(o.getStatus())).count() : 0);
			analytics.put("cancelledOrders", allOrders != null ? allOrders.stream().filter(o -> o != null && "Cancelled".equalsIgnoreCase(o.getStatus())).count() : 0);
			analytics.put("generatedAt", new java.util.Date());

			return ResponseEntity.ok(analytics);
		} catch (Exception e) {
			analytics.put("status", "ERROR");
			analytics.put("message", e.getMessage());
			return ResponseEntity.ok(analytics);
		}
	}
}
