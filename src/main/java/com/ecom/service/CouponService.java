package com.ecom.service;

import java.util.List;
import com.ecom.model.Coupon;

public interface CouponService {

	public Coupon saveCoupon(Coupon coupon);

	public List<Coupon> getAllCoupons();

	public Coupon getCouponById(Integer id);

	public Coupon getCouponByCode(String code);

	public void deleteCoupon(Integer id);

	public Boolean validateCoupon(String code, Double orderAmount);

	public List<Coupon> getActiveCoupons();
}
