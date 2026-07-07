package com.github.gokid96.e_commerce.coupon.application;

import com.github.gokid96.e_commerce.coupon.domain.Coupon;
import com.github.gokid96.e_commerce.coupon.domain.CouponRepository;
import com.github.gokid96.e_commerce.coupon.domain.CouponStatus;
import com.github.gokid96.e_commerce.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.support.database.DatabaseCleaner;
import com.github.gokid96.e_commerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CouponFacadeConcurrencyTest extends IntegrationTestSupport {

    @Autowired private CouponFacade couponFacade;
    @Autowired private CouponRepository couponRepository;
    @Autowired private DatabaseCleaner databaseCleaner;
    @Autowired private RedisKeyCleaner redisKeyCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
        redisKeyCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
        redisKeyCleaner.clean();
    }

    @DisplayName("같은 사용자가 동시에 여러 번 요청해도 한 번만 접수된다.")
    @Test
    void requestPublishUserCoupon_duplicated() throws InterruptedException {
        // given
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7)));

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        // when: 동일 유저의 동시 요청
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    couponFacade.requestPublishUserCoupon(CouponCriteria.PublishRequest.of(1L, coupon.getId()));
                    successCount.incrementAndGet();
                } catch (IllegalArgumentException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then: addIfAbsent로 1건만 접수
        assertThat(successCount.get()).isEqualTo(1);
    }

    @DisplayName("수량보다 많은 요청이 몰려도 수량만큼만 발급된다.")
    @Test
    void publishUserCoupons_limitedByQuantity() throws InterruptedException {
        // given: 수량 10, 요청 30명
        Coupon coupon = couponRepository.saveCoupon(
                Coupon.create("선착순 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7)));

        int userCount = 30;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(userCount);

        for (int i = 1; i <= userCount; i++) {
            long userId = i;
            executor.submit(() -> {
                try {
                    couponFacade.requestPublishUserCoupon(CouponCriteria.PublishRequest.of(userId, coupon.getId()));
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // when: 스케줄러 대신 직접 배치 발급 실행
        couponFacade.publishUserCoupons(CouponCriteria.Publish.of(100));

        // then: 수량(10)만큼만 발급
        List<com.github.gokid96.e_commerce.coupon.domain.UserCoupon> issued =
                couponRepository.findUserCouponsByCouponId(coupon.getId());
        assertThat(issued).hasSize(10);
    }
}