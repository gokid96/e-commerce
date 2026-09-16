package com.github.gokid96.e_commerce.coupon.domain;

import com.github.gokid96.e_commerce.coupon.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.coupon.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * 보유 쿠폰 조회는 QueryDSL 로 coupon 을 조인해 한 번의 쿼리로 처리한다.
 * {@code Projections.constructor} 는 타입만 맞으면 인자 순서가 뒤바뀌어도 컴파일되므로
 * 매핑 정확성을 통합 테스트로 고정한다.
 */
class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired private CouponService couponService;
    @Autowired private CouponRepository couponRepository;
    @Autowired private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    private Coupon publishableCoupon(String name, double discountRate, int quantity) {
        return couponRepository.saveCoupon(Coupon.create(
                name, discountRate, quantity, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(7)));
    }

    @DisplayName("보유 쿠폰을 쿠폰 정보와 함께 조회한다.")
    @Test
    void getUserCoupons() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 100);
        couponRepository.saveUserCoupon(UserCoupon.create(1L, coupon.getId()));

        List<CouponInfo.UserCoupon> result = couponService.getUserCoupons(1L);

        // 조인 프로젝션이 쿠폰명·할인율을 올바른 필드에 채우는지 확인한다.
        assertThat(result)
                .extracting("couponId", "couponName", "discountRate", "usedStatus")
                .containsExactly(tuple(coupon.getId(), "10% 할인", 0.1, UserCouponUsedStatus.UNUSED));
    }

    @DisplayName("사용한 쿠폰은 보유 쿠폰 목록에 포함되지 않는다.")
    @Test
    void getUserCouponsExcludesUsed() {
        Coupon unused = publishableCoupon("미사용", 0.1, 100);
        Coupon used = publishableCoupon("사용완료", 0.2, 100);
        couponRepository.saveUserCoupon(UserCoupon.create(1L, unused.getId()));

        UserCoupon usedUserCoupon = UserCoupon.create(1L, used.getId());
        usedUserCoupon.use();
        couponRepository.saveUserCoupon(usedUserCoupon);

        List<CouponInfo.UserCoupon> result = couponService.getUserCoupons(1L);

        assertThat(result)
                .extracting(CouponInfo.UserCoupon::getCouponName)
                .containsExactly("미사용");
    }

    @DisplayName("다른 사용자의 쿠폰은 조회되지 않는다.")
    @Test
    void getUserCouponsOfOtherUser() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 100);
        couponRepository.saveUserCoupon(UserCoupon.create(2L, coupon.getId()));

        assertThat(couponService.getUserCoupons(1L)).isEmpty();
    }

    @DisplayName("쿠폰을 발급하면 수량이 차감되고 보유 쿠폰이 생성된다.")
    @Test
    void publishUserCoupon() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 10);

        couponService.publishUserCoupon(CouponCommand.Publish.of(1L, coupon.getId()));

        assertThat(couponRepository.findCouponById(coupon.getId()).orElseThrow().getQuantity())
                .isEqualTo(9);
        assertThat(couponRepository.findOptionalUserCouponByUserIdAndCouponId(1L, coupon.getId()))
                .isPresent();
    }

    @DisplayName("이미 발급받은 쿠폰은 다시 발급되지 않는다.")
    @Test
    void publishUserCouponWhenAlreadyPublished() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 10);
        couponRepository.saveUserCoupon(UserCoupon.create(1L, coupon.getId()));

        CouponCommand.Publish command = CouponCommand.Publish.of(1L, coupon.getId());

        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 발급된 쿠폰입니다.");
    }

    @DisplayName("수량이 모두 소진된 쿠폰은 발급되지 않는다.")
    @Test
    void publishUserCouponWhenExhausted() {
        Coupon coupon = publishableCoupon("소진", 0.1, 0);

        CouponCommand.Publish command = CouponCommand.Publish.of(1L, coupon.getId());

        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰이 모두 소진되었습니다.");
    }

    @DisplayName("보유 쿠폰을 사용하면 사용 상태로 바뀐다.")
    @Test
    void useUserCoupon() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 10);
        UserCoupon userCoupon = couponRepository.saveUserCoupon(UserCoupon.create(1L, coupon.getId()));

        couponService.useUserCoupon(userCoupon.getId());

        assertThat(couponRepository.findUserCouponById(userCoupon.getId()).orElseThrow().getUsedStatus())
                .isEqualTo(UserCouponUsedStatus.USED);
    }

    @DisplayName("사용한 쿠폰을 취소하면 미사용 상태로 돌아온다.")
    @Test
    void cancelUserCoupon() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 10);
        UserCoupon userCoupon = UserCoupon.create(1L, coupon.getId());
        userCoupon.use();
        couponRepository.saveUserCoupon(userCoupon);

        couponService.cancelUserCoupon(userCoupon.getId());

        assertThat(couponRepository.findUserCouponById(userCoupon.getId()).orElseThrow().getUsedStatus())
                .isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("사용할 수 없는 쿠폰은 사용 가능 여부 조회에서 예외가 발생한다.")
    @Test
    void getUsableUserCouponWhenUsed() {
        Coupon coupon = publishableCoupon("10% 할인", 0.1, 10);
        UserCoupon userCoupon = UserCoupon.create(1L, coupon.getId());
        userCoupon.use();
        UserCoupon saved = couponRepository.saveUserCoupon(userCoupon);

        assertThatThrownBy(() -> couponService.getUsableUserCoupon(saved.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용할 수 없는 쿠폰입니다.");
    }
}
