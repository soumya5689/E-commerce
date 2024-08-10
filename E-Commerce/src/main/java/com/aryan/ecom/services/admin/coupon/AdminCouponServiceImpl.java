package com.aryan.ecom.services.admin.coupon;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.aryan.ecom.exceptions.ValidationException;
import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCouponServiceImpl implements AdminCouponService {
	private final CouponRepository couponRepository;

	public Coupon createCoupon(Coupon coupon) {
		if(couponRepository.existsByCode(coupon.getCode())) {
			throw new ValidationException("Coupon code already exists");
		} else {
			log.info("New Coupon Code Added: {}", coupon.getCode());
			return couponRepository.save(coupon);
		}
	}

	public List<Coupon> getAllCoupon() {
		log.info("Fetching all coupons.");
		return couponRepository.findAll();
	}
}
