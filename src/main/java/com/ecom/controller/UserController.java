package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.service.ReviewService;
import com.ecom.service.WishlistService;
import com.ecom.service.CouponService;
import com.ecom.model.Wishlist;
import com.ecom.model.Coupon;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private com.ecom.service.InvoiceService invoiceService;

	@Autowired
	private ProductOrderRepository productOrderRepository;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private com.ecom.service.ProductService productService;

	@Autowired
	private WishlistService wishlistService;

	@Autowired
	private CouponService couponService;

	@Autowired
	private com.ecom.repository.CouponRepository couponRepository;

	@org.springframework.beans.factory.annotation.Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@org.springframework.beans.factory.annotation.Value("${razorpay.key.secret}")
	private String razorpayKeySecret;


	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@Autowired
	private com.ecom.repository.GiftCardRepository giftCardRepository;

	@GetMapping("/offers")
	public String offersPage(Model m) {
		List<Coupon> activeCoupons = couponService.getActiveCoupons();
		m.addAttribute("activeCoupons", activeCoupons);
		java.util.List<com.ecom.model.GiftCard> giftCards = giftCardRepository.findByIsUsedFalse();
		m.addAttribute("giftCards", giftCards);
		return "user/offers";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			if (userDtls != null) {
				m.addAttribute("user", userDtls);
				Integer countCart = cartService.getCountCart(userDtls.getId());
				m.addAttribute("countCart", countCart != null ? countCart : 0);
			} else {
				m.addAttribute("countCart", 0);
			}
		} else {
			m.addAttribute("countCart", 0);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory != null ? allActiveCategory : java.util.Collections.emptyList());
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		if (user == null) return "redirect:/signin";

		List<Cart> carts = cartService.getCartsByUser(user.getId());
		if (carts == null) carts = new java.util.ArrayList<>();
		m.addAttribute("carts", carts);
		if (carts.size() > 0 && carts.get(carts.size() - 1) != null) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice != null ? totalOrderPrice : 0.0);
		} else {
			m.addAttribute("totalOrderPrice", 0.0);
		}
		return "user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		if (p == null) return null;
		String email = p.getName();
		return userService.getUserByEmail(email);
	}

	@Autowired
	private com.ecom.repository.UserRepository userRepository;

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		if (user == null) return "redirect:/signin";

		List<Cart> carts = cartService.getCartsByUser(user.getId());
		if (carts == null) carts = new java.util.ArrayList<>();
		m.addAttribute("carts", carts);

		Double orderPrice = 0.0;
		Double totalOrderPrice = 0.0;
		Double totalDiscount = 0.0;

		if (carts.size() > 0 && carts.get(carts.size() - 1) != null && carts.get(carts.size() - 1).getTotalOrderPrice() != null) {
			orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			totalOrderPrice = orderPrice + 250 + 100;

			Double couponDiscount = (Double) session.getAttribute("discount");
			Double giftCardAmount = (Double) session.getAttribute("giftCardAmount");
			Double loyaltyDiscount = (Double) session.getAttribute("loyaltyDiscount");

			if (couponDiscount != null) totalDiscount += couponDiscount;
			if (giftCardAmount != null) totalDiscount += giftCardAmount;
			if (loyaltyDiscount != null) totalDiscount += loyaltyDiscount;
		}

		m.addAttribute("orderPrice", orderPrice);
		m.addAttribute("totalDiscount", totalDiscount);
		m.addAttribute("totalOrderPrice", totalOrderPrice);
		m.addAttribute("finalPayable", Math.max(0.0, totalOrderPrice - totalDiscount));

		List<Coupon> activeCoupons = couponService.getActiveCoupons();
		m.addAttribute("activeCoupons", activeCoupons != null ? activeCoupons : new java.util.ArrayList<>());

		java.util.List<com.ecom.model.GiftCard> giftCards = giftCardRepository.findByIsUsedFalse();
		m.addAttribute("giftCards", giftCards != null ? giftCards : new java.util.ArrayList<>());

		return "/user/order";
	}

	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p, HttpSession session) throws Exception {
		UserDtls user = getLoggedInUserDetails(p);
		if (user == null) return "redirect:/signin";

		String couponCode = (String) session.getAttribute("couponCode");
		String giftCardCode = (String) session.getAttribute("giftCardCode");
		Integer loyaltyPointsToRedeem = (Integer) session.getAttribute("loyaltyPointsToRedeem");

		Double couponDiscount = (Double) session.getAttribute("discount");
		Double giftCardAmount = (Double) session.getAttribute("giftCardAmount");
		Double loyaltyDiscount = (Double) session.getAttribute("loyaltyDiscount");

		Double totalDiscount = 0.0;
		if (couponDiscount != null) totalDiscount += couponDiscount;
		if (giftCardAmount != null) totalDiscount += giftCardAmount;
		if (loyaltyDiscount != null) totalDiscount += loyaltyDiscount;

		StringBuilder promoSummary = new StringBuilder();
		if (couponCode != null) promoSummary.append("Coupon:").append(couponCode).append(" ");
		if (giftCardCode != null) promoSummary.append("GiftCard:").append(giftCardCode).append(" ");
		if (loyaltyPointsToRedeem != null) promoSummary.append("Points:").append(loyaltyPointsToRedeem);

		orderService.saveOrder(user.getId(), request, promoSummary.toString().trim(), totalDiscount);

		if (couponCode != null) {
			Coupon coupon = couponService.getCouponByCode(couponCode);
			if (coupon != null) {
				coupon.setUsageCount((coupon.getUsageCount() == null ? 0 : coupon.getUsageCount()) + 1);
				couponRepository.save(coupon);
			}
		}

		if (giftCardCode != null) {
			com.ecom.model.GiftCard gc = giftCardRepository.findByCodeAndIsUsedFalse(giftCardCode);
			if (gc != null) {
				gc.setIsUsed(true);
				gc.setUsedByUserId(user.getId());
				giftCardRepository.save(gc);
			}
		}

		int current = user.getLoyaltyPoints() == null ? 0 : user.getLoyaltyPoints();
		if (loyaltyPointsToRedeem != null && loyaltyPointsToRedeem > 0) {
			current = Math.max(0, current - loyaltyPointsToRedeem);
		}
		user.setLoyaltyPoints(current + 10);
		userRepository.save(user);

		session.removeAttribute("couponCode");
		session.removeAttribute("discount");
		session.removeAttribute("giftCardCode");
		session.removeAttribute("giftCardAmount");
		session.removeAttribute("loyaltyPointsToRedeem");
		session.removeAttribute("loyaltyDiscount");

		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model m, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		if (loginUser == null) return "redirect:/signin";

		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		if (orders == null) orders = new java.util.ArrayList<>();

		java.util.Map<Integer, com.ecom.model.ReturnRequest> returnRequestByOrderId = new java.util.HashMap<>();
		try {
			List<com.ecom.model.ReturnRequest> reqs = returnRequestRepository.findByUserId(loginUser.getId());
			if (reqs != null) {
				for (com.ecom.model.ReturnRequest req : reqs) {
					if (req != null && req.getOrder() != null && req.getOrder().getId() != null) {
						returnRequestByOrderId.put(req.getOrder().getId(), req);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		m.addAttribute("orders", orders);
		m.addAttribute("returnRequestByOrderId", returnRequestByOrderId);
		return "user/my_orders";
	}

	@GetMapping("/update-status")
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
		return "redirect:/user/user-orders";
	}

	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img, HttpSession session) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errorMsg", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile Updated");
		}
		return "redirect:/user/profile";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {
		UserDtls loggedInUserDetails = getLoggedInUserDetails(p);

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

		return "redirect:/user/profile";
	}

	@PostMapping("/add-review")
	public String addReview(@RequestParam Integer pid, @RequestParam Integer rating, @RequestParam String comment, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		com.ecom.model.Product product = productService.getProductById(pid);
		
		com.ecom.model.ProductReview review = new com.ecom.model.ProductReview();
		review.setUser(user);
		review.setProduct(product);
		review.setRating(rating);
		review.setComment(comment);
		
		reviewService.saveReview(review);
		
		session.setAttribute("succMsg", "Review submitted successfully!");
		return "redirect:/product/" + pid;
	}

	@GetMapping("/wishlist")
	public String loadWishlist(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Wishlist> wishlist = wishlistService.getWishlistByUser(user.getId());
		m.addAttribute("wishlists", wishlist);
		return "user/wishlist";
	}

	@GetMapping("/add-wishlist")
	public String addToWishlist(@RequestParam Integer pid, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		Wishlist saveWishlist = wishlistService.addToWishlist(pid, user.getId());
		if (saveWishlist == null) {
			session.setAttribute("errorMsg", "Item already in Wishlist or error occurred.");
		} else {
			session.setAttribute("succMsg", "Added to Wishlist!");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/delete-wishlist")
	public String removeFromWishlist(@RequestParam Integer pid, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		wishlistService.removeFromWishlist(pid, user.getId());
		session.setAttribute("succMsg", "Removed from Wishlist!");
		return "redirect:/user/wishlist";
	}

	@PostMapping("/apply-coupon")
	public String applyCoupon(@RequestParam String code, @RequestParam Double orderAmount, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		String cleanCode = code.trim().toUpperCase();

		// Check if this user has already used this coupon code on a previous order
		List<ProductOrder> userOrders = productOrderRepository.findByUserId(user.getId());
		boolean alreadyUsed = userOrders.stream()
				.anyMatch(o -> o.getCouponCode() != null && o.getCouponCode().toUpperCase().contains(cleanCode));

		if (alreadyUsed) {
			session.setAttribute("errorMsg", "You have already used coupon code '" + cleanCode + "' on a previous order!");
			session.removeAttribute("couponCode");
			session.removeAttribute("discount");
			return "redirect:/user/orders";
		}

		Boolean isValid = couponService.validateCoupon(cleanCode, orderAmount);
		if (isValid) {
			Coupon coupon = couponService.getCouponByCode(cleanCode);
			Double discount = orderAmount * (coupon.getDiscountPercentage() / 100.0);
			session.setAttribute("couponCode", cleanCode);
			session.setAttribute("discount", discount);
			session.setAttribute("succMsg", "Coupon '" + cleanCode + "' applied! Discount of " + coupon.getDiscountPercentage() + "% applied.");
		} else {
			session.setAttribute("errorMsg", "Invalid or expired Coupon Code!");
			session.removeAttribute("couponCode");
			session.removeAttribute("discount");
		}
		return "redirect:/user/orders";
	}

	@PostMapping("/create-payment-order")
	@org.springframework.web.bind.annotation.ResponseBody
	public String createPaymentOrder(@RequestParam Double amount) {
		try {
			com.razorpay.RazorpayClient client = new com.razorpay.RazorpayClient(razorpayKeyId, razorpayKeySecret);
			org.json.JSONObject options = new org.json.JSONObject();
			options.put("amount", (int) (amount * 100));
			options.put("currency", "INR");
			options.put("receipt", "txn_" + System.currentTimeMillis());
			
			com.razorpay.Order order = client.orders.create(options);
			return order.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"error\":\"" + e.getMessage() + "\"}";
		}
	}

	@PostMapping("/verify-payment")
	@org.springframework.web.bind.annotation.ResponseBody
	public java.util.Map<String, Object> verifyPayment(
			@RequestParam String razorpay_payment_id,
			@RequestParam String razorpay_order_id,
			@RequestParam String razorpay_signature) {
		
		java.util.Map<String, Object> response = new java.util.HashMap<>();
		try {
			String secret = razorpayKeySecret;
			String payload = razorpay_order_id + "|" + razorpay_payment_id;
			
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
			javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
					secret.getBytes("UTF-8"), "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] rawHmac = mac.doFinal(payload.getBytes("UTF-8"));
			
			StringBuilder hexString = new StringBuilder();
			for (byte b : rawHmac) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			
			String generatedSignature = hexString.toString();
			if (generatedSignature.equals(razorpay_signature)) {
				response.put("status", "success");
			} else {
				response.put("status", "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "error");
			response.put("message", e.getMessage());
		}
		return response;
	}

	// ============ REORDER ============
	@GetMapping("/reorder/{orderId}")
	public String reorder(@PathVariable Integer orderId, Principal p, HttpSession session) {
		com.ecom.model.ProductOrder order = orderService.getOrdersByOrderId(orderId.toString());
		if (order != null) {
			UserDtls user = getLoggedInUserDetails(p);
			cartService.saveCart(order.getProduct().getId(), user.getId());
			session.setAttribute("succMsg", "Product added to cart!");
		}
		return "redirect:/user/cart";
	}

	// ============ RETURN REQUEST ============
	@Autowired
	private com.ecom.repository.ReturnRequestRepository returnRequestRepository;

	@GetMapping("/my-returns")
	public String myReturns(Principal p, Model m) {
		UserDtls user = getLoggedInUserDetails(p);
		m.addAttribute("returnRequests", returnRequestRepository.findByUserId(user.getId()));
		return "user/my_returns";
	}

	@PostMapping("/submit-return")
	public String submitReturn(@RequestParam Integer orderId, @RequestParam String reason,
			Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		com.ecom.model.ProductOrder order = productOrderRepository.findById(orderId).orElse(null);
		if (order != null && order.getUser().getId().equals(user.getId())) {
			com.ecom.model.ReturnRequest rr = new com.ecom.model.ReturnRequest();
			rr.setOrder(order);
			rr.setUser(user);
			rr.setReason(reason);
			rr.setStatus("PENDING");
			rr.setRequestDate(new java.util.Date());
			returnRequestRepository.save(rr);

			// Update ProductOrder status
			order.setStatus("Return Requested");
			productOrderRepository.save(order);

			session.setAttribute("succMsg", "Return request submitted successfully!");
		} else {
			session.setAttribute("errorMsg", "Order not found!");
		}
		return "redirect:/user/user-orders";
	}

	// ============ GIFT CARD REDEMPTION ============
	@PostMapping("/apply-gift-card")
	public String applyGiftCard(@RequestParam String giftCode, HttpSession session) {
		com.ecom.model.GiftCard gc = giftCardRepository.findByCodeAndIsUsedFalse(giftCode.trim().toUpperCase());
		if (gc == null) {
			session.setAttribute("errorMsg", "Invalid or already used gift card!");
		} else if (gc.getExpiryDate() != null && gc.getExpiryDate().before(new java.util.Date())) {
			session.setAttribute("errorMsg", "Gift card has expired!");
		} else {
			session.setAttribute("giftCardCode", gc.getCode());
			session.setAttribute("giftCardAmount", gc.getAmount());
			session.setAttribute("succMsg", "Gift card applied! You save ₹" + gc.getAmount());
		}
		return "redirect:/user/orders";
	}

	@PostMapping("/remove-gift-card")
	public String removeGiftCard(HttpSession session) {
		session.removeAttribute("giftCardCode");
		session.removeAttribute("giftCardAmount");
		return "redirect:/user/orders";
	}

	// ============ LOYALTY POINTS ============
	@PostMapping("/redeem-points")
	public String redeemPoints(@RequestParam Integer points, Principal p, HttpSession session) {
		UserDtls user = getLoggedInUserDetails(p);
		int available = user.getLoyaltyPoints() == null ? 0 : user.getLoyaltyPoints();
		if (points > available) {
			session.setAttribute("errorMsg", "Not enough loyalty points!");
		} else {
			session.setAttribute("loyaltyPointsToRedeem", points);
			session.setAttribute("loyaltyDiscount", (double) points); // 1 point = ₹1
			session.setAttribute("succMsg", "Redeemed " + points + " points = ₹" + points + " discount!");
		}
		return "redirect:/user/orders";
	}

	@PostMapping("/remove-loyalty")
	public String removeLoyalty(HttpSession session) {
		session.removeAttribute("loyaltyPointsToRedeem");
		session.removeAttribute("loyaltyDiscount");
		return "redirect:/user/orders";
	}

	// ============ INVOICE PDF DOWNLOAD ============
	@GetMapping("/download-invoice/{orderId}")
	public org.springframework.http.ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer orderId, Principal p) {
		UserDtls user = getLoggedInUserDetails(p);
		com.ecom.model.ProductOrder order = productOrderRepository.findById(orderId).orElse(null);
		if (order == null || !order.getUser().getId().equals(user.getId())) {
			return org.springframework.http.ResponseEntity.notFound().build();
		}
		byte[] pdfBytes = invoiceService.generateInvoicePdf(order);
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
		headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment().filename("Invoice-" + order.getOrderId() + ".pdf").build());
		return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
	}

	// ============ 1-CLICK BUY NOW ============
	@GetMapping("/buy-now")
	public String buyNow(@RequestParam Integer pid, Principal p) {
		UserDtls user = getLoggedInUserDetails(p);
		cartService.saveCart(pid, user.getId());
		return "redirect:/user/orders";
	}

}
