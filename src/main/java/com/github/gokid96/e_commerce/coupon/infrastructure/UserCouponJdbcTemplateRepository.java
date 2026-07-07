package com.github.gokid96.e_commerce.coupon.infrastructure;

import com.github.gokid96.e_commerce.coupon.domain.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<UserCoupon> userCoupons) {
        String sql = "INSERT INTO user_coupon (user_id, coupon_id, used_status, issued_at) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, userCoupons, userCoupons.size(), (ps, uc) -> {
            ps.setLong(1, uc.getUserId());
            ps.setLong(2, uc.getCouponId());
            ps.setString(3, uc.getUsedStatus().name());
            ps.setTimestamp(4, Timestamp.valueOf(uc.getIssuedAt()));
        });
    }
}