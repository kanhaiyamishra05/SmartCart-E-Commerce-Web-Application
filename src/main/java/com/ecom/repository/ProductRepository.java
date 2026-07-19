package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	public Boolean existsByTitle(String title);

	List<Product> findByIsActiveTrue();

	Page<Product> findByIsActiveTrue(Pageable pageable);

	List<Product> findByCategory(String category);

	List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);

	Page<Product> findByCategory(Pageable pageable, String category);

	Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			Pageable pageable);

	Page<Product> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			Pageable pageable);

	@org.springframework.data.jpa.repository.Query("select p from Product p where p.isActive = true and p.discountPrice <= :maxPrice")
	Page<Product> findActiveProductsWithPrice(@org.springframework.data.repository.query.Param("maxPrice") Double maxPrice, Pageable pageable);

	@org.springframework.data.jpa.repository.Query("select p from Product p where p.isActive = true and p.category = :category and p.discountPrice <= :maxPrice")
	Page<Product> findActiveProductsByCategoryAndPrice(@org.springframework.data.repository.query.Param("category") String category, @org.springframework.data.repository.query.Param("maxPrice") Double maxPrice, Pageable pageable);

	@org.springframework.data.jpa.repository.Query("select p from Product p where p.isActive = true and (lower(p.title) like lower(concat('%', :ch, '%')) or lower(p.category) like lower(concat('%', :ch, '%'))) and p.discountPrice <= :maxPrice")
	Page<Product> searchActiveProductsWithPrice(@org.springframework.data.repository.query.Param("ch") String ch, @org.springframework.data.repository.query.Param("maxPrice") Double maxPrice, Pageable pageable);
}
