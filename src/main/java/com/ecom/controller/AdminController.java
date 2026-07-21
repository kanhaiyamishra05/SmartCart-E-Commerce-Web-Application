package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.service.CouponService;
import com.ecom.model.Coupon;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CouponService couponService;

	@Autowired
	private com.ecom.repository.FlashSaleRepository flashSaleRepository;

	@Autowired
	private com.ecom.repository.ReturnRequestRepository returnRequestRepository;

	@Autowired
	private com.ecom.repository.GiftCardRepository giftCardRepository;

	@Autowired
	private com.ecom.repository.SubscriberRepository subscriberRepository;

	@Autowired
	private com.ecom.repository.ProductReviewRepository productReviewRepository;

	@Autowired
	private com.ecom.repository.ProductVariantRepository productVariantRepository;

	@Autowired
	private com.ecom.repository.ProductOrderRepository productOrderRepository;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		// m.addAttribute("categorys", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
		List<Category> categorys = page.getContent();
		m.addAttribute("categorys", categorys);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category Name already exists");
		} else {

			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
			} else {

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Saved successfully");
			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {

			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ image.getOriginalFilename());

			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Saved Success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

//		List<Product> products = null;
//		if (ch != null && ch.length() > 0) {
//			products = productService.searchProduct(ch);
//		} else {
//			products = productService.getAllProducts();
//		}
//		m.addAttribute("products", products);

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		m.addAttribute("products", page.getContent());

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product delete success");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "Something wrong on server");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String getAllUsers(Model m, @RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType",type);
		m.addAttribute("users", users);
		return "/admin/users";
	}

	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "Account Status Updated");
		} else {
			session.setAttribute("errorMsg", "Something wrong on server");
		}
		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//		List<ProductOrder> allOrders = orderService.getAllOrders();
//		m.addAttribute("orders", allOrders);
//		m.addAttribute("srch", false);

		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errorMsg", "status not updated");
		}
		return "redirect:/admin/orders";
	}

	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		if (orderId != null && orderId.length() > 0) {

			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "Incorrect orderId");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}

			m.addAttribute("srch", true);
		} else {
//			List<ProductOrder> allOrders = orderService.getAllOrders();
//			m.addAttribute("orders", allOrders);
//			m.addAttribute("srch", false);

			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page);
			m.addAttribute("srch", false);

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());

		}
		return "/admin/orders";

	}

	@GetMapping("/add-admin")
	public String loadAdminAdd() {
		return "/admin/add_admin";
	}

	@PostMapping("/save-admin")
	public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

//				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("succMsg", "Register successfully");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/add-admin";
	}

	@GetMapping("/profile")
	public String profile() {
		return "/admin/profile";
	}

	@GetMapping("/sales-report")
	public String salesReport(Model m) {
		List<ProductOrder> orders = orderService.getAllOrders();
		double totalRevenue = 0.0;
		long totalOrders = orders.size();
		long totalUsers = userService.getUsers("ROLE_USER").size();

		for (ProductOrder o : orders) {
			if (!"Cancelled".equalsIgnoreCase(o.getStatus())) {
				double orderTotal = o.getPrice() * o.getQuantity();
				if (o.getDiscount() != null) {
					orderTotal -= o.getDiscount();
				}
				totalRevenue += orderTotal;
			}
		}

		m.addAttribute("totalRevenue", totalRevenue);
		m.addAttribute("totalOrders", totalOrders);
		m.addAttribute("totalUsers", totalUsers);
		return "admin/sales_report";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/admin/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errorMsg", "Password not updated !! Error in server");
			} else {
				session.setAttribute("succMsg", "Password Updated sucessfully");
			}
		} else {
			session.setAttribute("errorMsg", "Current Password incorrect");
		}

		return "redirect:/admin/profile";
	}

	@GetMapping("/coupons")
	public String coupons(Model m) {
		m.addAttribute("coupons", couponService.getAllCoupons());
		return "admin/coupons";
	}

	@PostMapping("/save-coupon")
	public String saveCoupon(@ModelAttribute Coupon coupon, HttpSession session) {
		try {
			coupon.setIsActive(true);
			couponService.saveCoupon(coupon);
			session.setAttribute("succMsg", "Coupon saved successfully!");
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Failed to save coupon. Make sure code is unique!");
		}
		return "redirect:/admin/coupons";
	}

	@GetMapping("/delete-coupon/{id}")
	public String deleteCoupon(@PathVariable Integer id, HttpSession session) {
		couponService.deleteCoupon(id);
		session.setAttribute("succMsg", "Coupon deleted successfully!");
		return "redirect:/admin/coupons";
	}

	@GetMapping("/sales-stats")
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, Object> getSalesStats() {
		java.util.Map<String, Object> stats = new java.util.HashMap<>();
		List<ProductOrder> orders = orderService.getAllOrders();
		
		double totalRevenue = 0.0;
		long totalOrders = orders.size();
		long totalUsers = userService.getUsers("ROLE_USER").size();
		
		java.util.Map<String, Double> monthlySales = new java.util.LinkedHashMap<>();
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		for (String m : months) {
			monthlySales.put(m, 0.0);
		}

		for (ProductOrder o : orders) {
			if (!"Cancelled".equalsIgnoreCase(o.getStatus())) {
				double orderTotal = o.getPrice() * o.getQuantity();
				if (o.getDiscount() != null) {
					orderTotal -= o.getDiscount();
				}
				totalRevenue += orderTotal;
				
				if (o.getOrderDate() != null) {
					String monthName = o.getOrderDate().getMonth().name().substring(0, 3);
					String monthKey = monthName.charAt(0) + monthName.substring(1).toLowerCase();
					monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + orderTotal);
				}
			}
		}

		stats.put("totalRevenue", totalRevenue);
		stats.put("totalOrders", totalOrders);
		stats.put("totalUsers", totalUsers);
		stats.put("monthlySales", monthlySales);
		
		return stats;
	}

	// ============ FLASH SALES ============
	@GetMapping("/flash-sales")
	public String flashSalesPage(Model m) {
		m.addAttribute("flashSales", flashSaleRepository.findAll());
		m.addAttribute("flashSale", new com.ecom.model.FlashSale());
		return "admin/flash_sales";
	}

	@PostMapping("/save-flash-sale")
	public String saveFlashSale(@ModelAttribute com.ecom.model.FlashSale flashSale, HttpSession session) {
		flashSale.setIsActive(true);
		flashSaleRepository.save(flashSale);
		session.setAttribute("succMsg", "Flash sale created successfully!");
		return "redirect:/admin/flash-sales";
	}

	@GetMapping("/toggle-flash-sale/{id}")
	public String toggleFlashSale(@PathVariable Integer id, HttpSession session) {
		com.ecom.model.FlashSale fs = flashSaleRepository.findById(id).orElse(null);
		if (fs != null) {
			fs.setIsActive(!fs.getIsActive());
			flashSaleRepository.save(fs);
			session.setAttribute("succMsg", "Flash sale status updated!");
		}
		return "redirect:/admin/flash-sales";
	}

	@GetMapping("/delete-flash-sale/{id}")
	public String deleteFlashSale(@PathVariable Integer id, HttpSession session) {
		flashSaleRepository.deleteById(id);
		session.setAttribute("succMsg", "Flash sale deleted!");
		return "redirect:/admin/flash-sales";
	}

	// ============ RETURN REQUESTS ============
	@GetMapping("/returns")
	public String returnsPage(Model m) {
		m.addAttribute("returnRequests", returnRequestRepository.findAll());
		return "admin/returns";
	}

	@PostMapping("/update-return/{id}")
	public String updateReturnStatus(@PathVariable Integer id,
			@RequestParam String status,
			@RequestParam(required = false) String adminNote,
			HttpSession session) {
		com.ecom.model.ReturnRequest rr = returnRequestRepository.findById(id).orElse(null);
		if (rr != null) {
			rr.setStatus(status);
			rr.setAdminNote(adminNote);
			returnRequestRepository.save(rr);

			// Update associated ProductOrder status
			com.ecom.model.ProductOrder order = rr.getOrder();
			if (order != null) {
				if ("APPROVED".equalsIgnoreCase(status)) {
					order.setStatus("Return Approved");
				} else if ("REFUNDED".equalsIgnoreCase(status)) {
					order.setStatus("Returned & Refunded");
				} else if ("REJECTED".equalsIgnoreCase(status)) {
					order.setStatus("Return Rejected");
				} else if ("PENDING".equalsIgnoreCase(status)) {
					order.setStatus("Return Requested");
				}
				productOrderRepository.save(order);
			}

			session.setAttribute("succMsg", "Return request " + status + "!");
		}
		return "redirect:/admin/returns";
	}

	// ============ GIFT CARDS ============
	@GetMapping("/gift-cards")
	public String giftCardsPage(Model m) {
		m.addAttribute("giftCards", giftCardRepository.findAll());
		m.addAttribute("giftCard", new com.ecom.model.GiftCard());
		return "admin/gift_cards";
	}

	@PostMapping("/save-gift-card")
	public String saveGiftCard(@ModelAttribute com.ecom.model.GiftCard giftCard, HttpSession session) {
		// Auto-generate code if blank
		if (giftCard.getCode() == null || giftCard.getCode().isBlank()) {
			giftCard.setCode("GIFT-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		}
		giftCard.setIsUsed(false);
		giftCard.setCreatedAt(new java.util.Date());
		giftCardRepository.save(giftCard);
		session.setAttribute("succMsg", "Gift card created!");
		return "redirect:/admin/gift-cards";
	}

	@GetMapping("/delete-gift-card/{id}")
	public String deleteGiftCard(@PathVariable Integer id, HttpSession session) {
		giftCardRepository.deleteById(id);
		session.setAttribute("succMsg", "Gift card deleted!");
		return "redirect:/admin/gift-cards";
	}

	// ============ NEWSLETTER ============
	@GetMapping("/newsletter")
	public String newsletterPage(Model m) {
		m.addAttribute("subscribers", subscriberRepository.findByIsActiveTrue());
		return "admin/newsletter";
	}

	@PostMapping("/send-newsletter")
	public String sendNewsletter(@RequestParam String subject, @RequestParam String message, HttpSession session) {
		java.util.List<com.ecom.model.Subscriber> subs = subscriberRepository.findByIsActiveTrue();
		int sent = 0;
		for (com.ecom.model.Subscriber sub : subs) {
			try {
				commonUtil.sendNewsletterMail(sub.getEmail(), subject, message);
				sent++;
			} catch (Exception e) {
				System.out.println("Failed to send newsletter to: " + sub.getEmail());
			}
		}
		session.setAttribute("succMsg", "Newsletter sent to " + sent + " subscribers!");
		return "redirect:/admin/newsletter";
	}

	// ============ REVIEW MODERATION ============
	@GetMapping("/reviews")
	public String reviewsPage(Model m) {
		m.addAttribute("reviews", productReviewRepository.findAllByOrderByReviewDateDesc());
		return "admin/reviews";
	}

	@GetMapping("/approve-review/{id}")
	public String approveReview(@PathVariable Integer id, HttpSession session) {
		com.ecom.model.ProductReview review = productReviewRepository.findById(id).orElse(null);
		if (review != null) {
			review.setIsApproved(true);
			productReviewRepository.save(review);
			session.setAttribute("succMsg", "Review approved!");
		}
		return "redirect:/admin/reviews";
	}

	@GetMapping("/reject-review/{id}")
	public String rejectReview(@PathVariable Integer id, HttpSession session) {
		com.ecom.model.ProductReview review = productReviewRepository.findById(id).orElse(null);
		if (review != null) {
			review.setIsApproved(false);
			productReviewRepository.save(review);
			session.setAttribute("succMsg", "Review hidden!");
		}
		return "redirect:/admin/reviews";
	}

	@GetMapping("/delete-review/{id}")
	public String deleteReview(@PathVariable Integer id, HttpSession session) {
		productReviewRepository.deleteById(id);
		session.setAttribute("succMsg", "Review deleted!");
		return "redirect:/admin/reviews";
	}

	// ============ COUPON USAGE REPORT ============
	@GetMapping("/coupon-report")
	public String couponReport(Model m) {
		List<Coupon> coupons = couponService.getAllCoupons();
		int totalUses = coupons.stream()
				.mapToInt(coupon -> coupon.getUsageCount() == null ? 0 : coupon.getUsageCount())
				.sum();
		long activeCouponCount = coupons.stream()
				.filter(coupon -> Boolean.TRUE.equals(coupon.getIsActive()))
				.count();

		m.addAttribute("coupons", coupons);
		m.addAttribute("totalUses", totalUses);
		m.addAttribute("activeCouponCount", activeCouponCount);
		return "admin/coupon_report";
	}

	// ============ LOW STOCK ALERTS ============
	@GetMapping("/low-stock")
	public String lowStockPage(Model m) {
		java.util.List<Product> lowStock = productService.getAllProducts().stream()
				.filter(p -> p.getIsActive() && p.getStock() <= 5)
				.sorted((a, b) -> Integer.compare(a.getStock(), b.getStock()))
				.collect(java.util.stream.Collectors.toList());
		m.addAttribute("lowStockProducts", lowStock);
		return "admin/low_stock";
	}

	// ============ PRODUCT VARIANTS ============
	@GetMapping("/variants/{productId}")
	public String variantsPage(@PathVariable Integer productId, Model m) {
		m.addAttribute("product", productService.getProductById(productId));
		m.addAttribute("variants", productVariantRepository.findByProductId(productId));
		m.addAttribute("variant", new com.ecom.model.ProductVariant());
		return "admin/variants";
	}

	@PostMapping("/save-variant/{productId}")
	public String saveVariant(@PathVariable Integer productId,
			@ModelAttribute com.ecom.model.ProductVariant variant, HttpSession session) {
		variant.setProductId(productId);
		productVariantRepository.save(variant);
		session.setAttribute("succMsg", "Variant saved!");
		return "redirect:/admin/variants/" + productId;
	}

	@GetMapping("/delete-variant/{id}/{productId}")
	public String deleteVariant(@PathVariable Integer id, @PathVariable Integer productId, HttpSession session) {
		productVariantRepository.deleteById(id);
		session.setAttribute("succMsg", "Variant deleted!");
		return "redirect:/admin/variants/" + productId;
	}

	// ============ BULK PRODUCT UPLOAD ============
	@GetMapping("/bulk-upload")
	public String bulkUploadPage() {
		return "admin/bulk_upload";
	}

	@PostMapping("/bulk-upload")
	public String processBulkUpload(@RequestParam("csvFile") org.springframework.web.multipart.MultipartFile csvFile,
			HttpSession session) {
		if (csvFile.isEmpty()) {
			session.setAttribute("errorMsg", "Please select a CSV file!");
			return "redirect:/admin/bulk-upload";
		}
		int saved = 0;
		int skipped = 0;
		try (java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(csvFile.getInputStream()))) {
			String line;
			boolean isHeader = true;
			while ((line = reader.readLine()) != null) {
				if (isHeader) { isHeader = false; continue; } // skip header row
				String[] parts = line.split(",");
				if (parts.length < 6) continue;
				String title = parts[0].trim();
				String description = parts[1].trim();
				String category = parts[2].trim();
				Double price = Double.parseDouble(parts[3].trim());
				Integer stock = Integer.parseInt(parts[4].trim());
				Integer discount = Integer.parseInt(parts[5].trim());
				com.ecom.model.Product p = new com.ecom.model.Product();
				p.setTitle(title);
				p.setDescription(description);
				p.setCategory(category);
				p.setPrice(price);
				p.setStock(stock);
				p.setDiscount(discount);
				Double discountAmt = price * (discount / 100.0);
				p.setDiscountPrice(Math.round((price - discountAmt) * 100.0) / 100.0);
				p.setIsActive(true);
				p.setImage("default.png");
				productService.saveProduct(p);
				saved++;
			}
		} catch (Exception e) {
			session.setAttribute("errorMsg", "Error processing CSV: " + e.getMessage());
			return "redirect:/admin/bulk-upload";
		}
		session.setAttribute("succMsg", saved + " products uploaded successfully!");
		return "redirect:/admin/bulk-upload";
	}

}
