package com.ecom.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

/**
 * Centralized REST API Controller for SmartCart.
 * Consolidates store catalog, orders, coupons, flash sales, and analytics into unified JSON endpoints.
 */
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
	 * Returns store catalog summary, active deals, categories, and metrics in a single JSON payload.
	 */
	@GetMapping("/store-overview")
	public ResponseEntity<Map<String, Object>> getStoreOverview() {
		Map<String, Object> data = new HashMap<>();

		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProducts("");
		List<Coupon> activeCoupons = couponService.getActiveCoupons();
		List<FlashSale> activeFlashSales = flashSaleRepository.findByIsActiveTrueAndEndTimeAfter(new java.util.Date());

		data.put("status", "SUCCESS");
		data.put("totalCategories", categories.size());
		data.put("totalActiveProducts", products.size());
		data.put("categories", categories);
		data.put("activeCouponsCount", activeCoupons.size());
		data.put("activeCoupons", activeCoupons);
		data.put("activeFlashSalesCount", activeFlashSales.size());
		data.put("activeFlashSales", activeFlashSales);
		data.put("timestamp", new java.util.Date());

		return ResponseEntity.ok(data);
	}

	/**
	 * Centralized Product Search & Filter API
	 */
	@GetMapping("/products")
	public ResponseEntity<Map<String, Object>> searchProducts(
			@RequestParam(defaultValue = "") String ch,
			@RequestParam(defaultValue = "") String category) {
		Map<String, Object> response = new HashMap<>();
		List<Product> products = productService.getAllActiveProducts(category);
		response.put("query", ch);
		response.put("category", category);
		response.put("count", products.size());
		response.put("products", products);
		return ResponseEntity.ok(response);
	}

	/**
	 * Centralized Analytics Endpoint (For Admin & External Dashboards)
	 */
	@GetMapping("/analytics")
	public ResponseEntity<Map<String, Object>> getAnalytics() {
		Map<String, Object> analytics = new HashMap<>();
		List<ProductOrder> allOrders = productOrderRepository.findAll();
		double totalRevenue = allOrders.stream()
				.filter(o -> !"Cancelled".equalsIgnoreCase(o.getStatus()))
				.mapToDouble(o -> o.getPrice() * o.getQuantity())
				.sum();

		analytics.put("totalOrders", allOrders.size());
		analytics.put("totalRevenueINR", totalRevenue);
		analytics.put("totalProducts", productService.getAllProducts().size());
		analytics.put("registeredUsers", userService.getUsers("ROLE_USER").size());
		analytics.put("pendingOrders", allOrders.stream().filter(o -> "In Progress".equalsIgnoreCase(o.getStatus())).count());
		analytics.put("deliveredOrders", allOrders.stream().filter(o -> "Delivered".equalsIgnoreCase(o.getStatus())).count());
		analytics.put("cancelledOrders", allOrders.stream().filter(o -> "Cancelled".equalsIgnoreCase(o.getStatus())).count());
		analytics.put("generatedAt", new java.util.Date());

		return ResponseEntity.ok(analytics);
	}
}
