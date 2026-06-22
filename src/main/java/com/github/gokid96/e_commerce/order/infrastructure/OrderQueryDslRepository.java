package com.github.gokid96.e_commerce.order.infrastructure;

import com.github.gokid96.e_commerce.order.domain.OrderCommand;
import com.github.gokid96.e_commerce.order.domain.OrderInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.gokid96.e_commerce.order.domain.QOrder.order;
import static com.github.gokid96.e_commerce.order.domain.QOrderProduct.orderProduct;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command) {
        return queryFactory.select(
                        Projections.constructor(
                                OrderInfo.PaidProduct.class,
                                orderProduct.productId,
                                orderProduct.quantity.sumAggregate().as("quantity")
                        ))
                .from(order)
                .join(order.orderProducts, orderProduct)
                .where(
                        order.orderStatus.eq(command.getStatus()),
                        order.paidAt.between(
                                command.getPaidAt().minusDays(1).atStartOfDay(),
                                command.getPaidAt().atStartOfDay()
                        )
                )
                .groupBy(orderProduct.productId)
                .fetch();
    }
}



