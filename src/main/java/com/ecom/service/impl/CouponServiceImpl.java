package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecom.model.Coupon;
import com.ecom.repository.CouponRepository;
import com.ecom.service.CouponService;

@Service
public class CouponServiceImpl implements CouponService {

	@Autowired
	private CouponRepository couponRepository;

	@Override
	public Coupon saveCoupon(Coupon coupon) {
		return couponRepository.save(coupon);
	}

	@Override
	public List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	@Override
	public Coupon getCouponById(Integer id) {
		return couponRepository.findById(id).orElse(null);
	}

	@Override
	public Coupon getCouponByCode(String code) {
		return couponRepository.findByCodeIgnoreCase(code);
	}

	@Override
	public void deleteCoupon(Integer id) {
		couponRepository.deleteById(id);
	}

	@Override
	public Boolean validateCoupon(String code, Double orderAmount) {
		Coupon coupon = couponRepository.findByCodeIgnoreCase(code);
		if (coupon == null || !coupon.getIsActive()) {
			return false;
		}
		// check expiry
		if (coupon.getExpiryDate() != null && coupon.getExpiryDate().before(new Date())) {
			return false;
		}
		// check min amount
		if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount()) {
			return false;
		}
		return true;
	}

	@Override
	public List<Coupon> getActiveCoupons() {
		List<Coupon> activeCoupons = couponRepository.findByIsActiveTrue();
		Date today = new Date();
		return activeCoupons.stream()
				.filter(c -> c.getExpiryDate() == null || !c.getExpiryDate().before(today))
				.collect(java.util.stream.Collectors.toList());
	}
}
