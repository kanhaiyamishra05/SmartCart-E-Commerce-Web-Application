package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private com.ecom.repository.CategoryRepository categoryRepository;

	@Autowired
	private com.ecom.repository.ProductRepository productRepository;

	@Autowired
	private com.ecom.repository.ProductReviewRepository productReviewRepository;

	@Value("${app.admin.email}")
	private String adminEmail;

	@Value("${app.admin.password}")
	private String adminPassword;

	private void saveCategoryIfNotExist(String name, String imageName) {
		if (!categoryRepository.existsByName(name)) {
			categoryRepository.save(new com.ecom.model.Category(null, name, imageName, true));
			System.out.println("Seeded Category: " + name);
		}
	}

	private void saveProductIfNotExist(String title, String description, String category, Double price, int stock, String image, int discount) {
		if (!productRepository.existsByTitle(title)) {
			com.ecom.model.Product p = new com.ecom.model.Product();
			p.setTitle(title);
			p.setDescription(description);
			p.setCategory(category);
			p.setPrice(price);
			p.setStock(stock);
			p.setImage(image);
			p.setDiscount(discount);
			Double discountPrice = Math.round(price * (1 - (discount / 100.0)) * 100.0) / 100.0;
			p.setDiscountPrice(discountPrice);
			p.setIsActive(true);
			productRepository.save(p);
			System.out.println("Seeded Product: " + title);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		// Seed Categories
		saveCategoryIfNotExist("Electronics", "laptop.jpg");
		saveCategoryIfNotExist("Mobile Phones", "mobile.png");
		saveCategoryIfNotExist("Apparel", "pant.png");
		saveCategoryIfNotExist("Home Appliances", "appli.png");
		saveCategoryIfNotExist("Footwear", "canvas.jfif");

		// Seed Products
		// Electronics
		saveProductIfNotExist("HP Pavilion Laptop", "Intel Core i5, 16GB RAM, 512GB SSD, Windows 11", "Electronics", 55000.0, 45, "hp laptop.jpg", 10);
		saveProductIfNotExist("UltraWide Gaming Monitor", "34-inch Curved Gaming Monitor with 144Hz refresh rate", "Electronics", 28000.0, 30, "monitor.jpg", 15);
		saveProductIfNotExist("Professional Workstation Laptop", "High performance Laptop for creators and developers", "Electronics", 85000.0, 20, "laptop.jpg", 8);

		// Mobile Phones
		saveProductIfNotExist("iPhone 14 Pro", "Apple iPhone 14 Pro, 128GB, Space Black", "Mobile Phones", 95000.0, 50, "iphone 14.jpg", 12);
		saveProductIfNotExist("OnePlus Nord 3", "OnePlus Nord 3 5G, 16GB RAM, 256GB Storage", "Mobile Phones", 34000.0, 65, "oneplus mobile.jpg", 10);
		saveProductIfNotExist("Samsung Galaxy M34", "Samsung Galaxy M34 5G, 6GB RAM, 128GB Storage", "Mobile Phones", 18000.0, 80, "mobile.jpg", 5);

		// Apparel
		saveProductIfNotExist("Premium Blue Shirt", "100% Cotton Slim Fit Casual shirt for men", "Apparel", 1800.0, 120, "blue shirt.jfif", 15);
		saveProductIfNotExist("Men's Printed Shorts", "Comfortable beachwear and casual printed shorts", "Apparel", 999.0, 200, "printed short.jfif", 20);
		saveProductIfNotExist("Slim Fit Blue Jeans", "Durable stretch denim casual slim fit jeans", "Apparel", 2499.0, 150, "jeans blue.jfif", 10);
		saveProductIfNotExist("Designer Lehenga Choli", "Elegant party wear designer Lehenga Choli set", "Apparel", 7999.0, 40, "lehenga.jfif", 25);
		saveProductIfNotExist("Women's Printed Kurti", "Traditional cotton printed casual Kurti top", "Apparel", 1299.0, 180, "kruti.jfif", 30);

		// Home Appliances
		saveProductIfNotExist("Double Door Smart Fridge", "Frost-free double door smart refrigerator with inverter compressor", "Home Appliances", 35000.0, 25, "fridge.png", 12);
		saveProductIfNotExist("Automatic Washing Machine", "Front load fully automatic washing machine with steam wash", "Home Appliances", 29999.0, 35, "washing_machine.png", 15);
		saveProductIfNotExist("Mixer Grinder Juicer", "750W heavy duty motor mixer grinder with 3 stainless steel jars", "Home Appliances", 3499.0, 100, "grinder.jpg", 20);

		// Footwear
		saveProductIfNotExist("Sporty Canvas Shoes", "Lightweight breathable running and walking canvas shoes", "Footwear", 1499.0, 110, "canvas.jfif", 10);
		saveProductIfNotExist("Men's Leather Loafers", "Premium synthetic leather slip-on formal loafers", "Footwear", 1999.0, 140, "lofer.jfif", 15);
		saveProductIfNotExist("White Sport Sneakers", "Classic retro style white sneakers for outdoor sports", "Footwear", 2999.0, 90, "white shoe.jfif", 20);
		saveProductIfNotExist("Men's Official Shoes", "Elegant formal lace-up leather official dress shoes", "Footwear", 2499.0, 100, "official shoe.jfif", 10);
		UserDtls admin = userRepository.findByEmail(adminEmail);
		if (admin == null) {
			admin = new UserDtls();
			admin.setName("System Admin");
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setMobileNumber("9999999999");
			admin.setAddress("Admin Head Office");
			admin.setCity("Delhi");
			admin.setState("Delhi");
			admin.setPincode("110001");
			admin.setProfileImage("default.jpg");
			admin.setRole("ROLE_ADMIN");
			admin.setIsEnable(true);
			admin.setAccountNonLocked(true);
			admin.setFailedAttempt(0);
			
			userRepository.save(admin);
			System.out.println("==================================================");
			System.out.println("Default Admin Account Created: " + adminEmail);
			System.out.println("==================================================");
		} else {
			boolean updated = false;
			if (!"ROLE_ADMIN".equals(admin.getRole())) {
				admin.setRole("ROLE_ADMIN");
				updated = true;
			}
			if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
				admin.setPassword(passwordEncoder.encode(adminPassword));
				updated = true;
			}
			if (Boolean.FALSE.equals(admin.getAccountNonLocked())) {
				admin.setAccountNonLocked(true);
				updated = true;
			}
			if (admin.getFailedAttempt() != null && admin.getFailedAttempt() > 0) {
				admin.setFailedAttempt(0);
				updated = true;
			}
			if (Boolean.FALSE.equals(admin.getIsEnable())) {
				admin.setIsEnable(true);
				updated = true;
			}
			if (updated) {
				userRepository.save(admin);
				System.out.println("==================================================");
				System.out.println("Admin Account Status Synchronized/Unlocked on Startup");
				System.out.println("==================================================");
			}
		}

		// Seed initial approved customer reviews if empty
		if (productReviewRepository.count() == 0) {
			java.util.List<com.ecom.model.Product> productsList = productRepository.findAll();
			if (admin != null && !productsList.isEmpty()) {
				productReviewRepository.save(new com.ecom.model.ProductReview(null, admin, productsList.get(0), 5, "Excellent build quality & super fast shipping! Very satisfied.", new java.util.Date(), true));
				if (productsList.size() > 1) {
					productReviewRepository.save(new com.ecom.model.ProductReview(null, admin, productsList.get(1), 5, "Great deal! Got it during the flash sale, packaging was top-notch.", new java.util.Date(), true));
				}
				if (productsList.size() > 2) {
					productReviewRepository.save(new com.ecom.model.ProductReview(null, admin, productsList.get(2), 4, "Value for money product. Delivery was on time and support was helpful.", new java.util.Date(), true));
				}
				System.out.println("Seeded initial approved customer reviews.");
			}
		}
	}
}
