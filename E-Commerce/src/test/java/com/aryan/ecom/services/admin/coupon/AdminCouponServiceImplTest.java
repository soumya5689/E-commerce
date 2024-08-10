package com.aryan.ecom.services.admin.coupon;

import com.aryan.ecom.model.Coupon;
import com.aryan.ecom.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminCouponServiceImplTest {

    AutoCloseable autoCloseable;
    Coupon coupon;
    @Mock
    private CouponRepository couponRepository;

    private AdminCouponService adminCouponService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminCouponService = new AdminCouponServiceImpl(couponRepository);

        LocalDate now = LocalDate.now();
        LocalDate expirationDateLocal = now.plusDays(15);
        Date expirationDate = Date.from(expirationDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        coupon = Coupon.builder()
                .name("Early Offers")
                .expirationDate(expirationDate)
                .discount(15L)
                .code("FLAT15")
                .build();

    }

    @AfterEach
    void tearDown() throws Exception{
        autoCloseable.close();
    }

    @Test
    void createCoupon() {
        when(couponRepository.existsByCode("FLAT15")).thenReturn(false);
        when(couponRepository.save(any())).thenReturn(coupon);
        Coupon savedCoupon = adminCouponService.createCoupon(coupon);
        assertEquals(coupon.getCode(),savedCoupon.getCode());
    }

    @Test
    void getAllCoupon() {
        List<Coupon> coupons = List.of(coupon,coupon);
        when(couponRepository.findAll()).thenReturn(coupons);
        assertEquals(coupons.size(),adminCouponService.getAllCoupon().size());
    }
}