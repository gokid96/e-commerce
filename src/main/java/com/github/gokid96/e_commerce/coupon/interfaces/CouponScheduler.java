package com.github.gokid96.e_commerce.coupon.interfaces;

import com.github.gokid96.e_commerce.coupon.application.CouponCriteria;
import com.github.gokid96.e_commerce.coupon.application.CouponFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.github.gokid96.e_commerce.coupon.application.CouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponFacade couponFacade;

    @Scheduled(cron = "0 * * * * *")
    public void publishUserCoupons() {
        log.info("사용자 쿠폰 발급 스케줄러 실행");
        try {
            couponFacade.publishUserCoupons(CouponCriteria.Publish.of(MAX_PUBLISH_COUNT_PER_REQUEST));
            log.info("사용자 쿠폰 발급 스케줄러 완료");
        } catch (Exception e) {
            log.error("사용자 쿠폰 발급 스케줄러 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "30 */5 * * * *")
    public void finishedPublishCoupons() {
        log.info("쿠폰 발급 마감 스케줄러 실행");
        try {
            couponFacade.finishedPublishCoupons();
            log.info("쿠폰 발급 마감 스케줄러 완료");
        } catch (Exception e) {
            log.error("쿠폰 발급 마감 스케줄러 실행 중 오류 발생", e);
        }
    }
}