package com.aryan.ecom.services.admin.coupon;

import java.util.List;

import com.aryan.ecom.model.Coupon;

public interface AdminCouponService {
	Coupon createCoupon(Coupon coupon);
	List<Coupon> getAllCoupon();
}
