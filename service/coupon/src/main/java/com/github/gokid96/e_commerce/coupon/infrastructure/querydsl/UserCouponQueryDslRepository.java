package com.github.gokid96.e_commerce.coupon.infrastructure.querydsl;

import com.github.gokid96.e_commerce.coupon.domain.CouponInfo;
import com.github.gokid96.e_commerce.coupon.domain.UserCouponUsedStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.gokid96.e_commerce.coupon.domain.QCoupon.coupon;
import static com.github.gokid96.e_commerce.coupon.domain.QUserCoupon.userCoupon;

@Repository
@RequiredArgsConstructor
public class UserCouponQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 보유 쿠폰을 쿠폰 정보와 조인해 한 번의 쿼리로 조회한다.
     * user_coupon 은 coupon 을 연관관계 없이 ID 로만 참조하므로 on 절로 직접 조인한다.
     */
    public List<CouponInfo.UserCoupon> findUserCouponsByUserIdAndUsedStatusIn(
            Long userId, List<UserCouponUsedStatus> usedStatuses) {
        return queryFactory
                .select(Projections.constructor(
                        CouponInfo.UserCoupon.class,
                        userCoupon.id,
                        coupon.id,
                        coupon.name,
                        coupon.discountRate,
                        userCoupon.usedStatus
                ))
                .from(userCoupon)
                .innerJoin(coupon).on(userCoupon.couponId.eq(coupon.id))
                .where(
                        userCoupon.userId.eq(userId),
                        userCoupon.usedStatus.in(usedStatuses)
                )
                .fetch();
    }
}
