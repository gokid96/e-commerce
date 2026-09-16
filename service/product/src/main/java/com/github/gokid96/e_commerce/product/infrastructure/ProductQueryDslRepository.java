package com.github.gokid96.e_commerce.product.infrastructure;

import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.github.gokid96.e_commerce.product.domain.product.ProductInfo;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.gokid96.e_commerce.product.domain.product.QProduct.product;
import static com.github.gokid96.e_commerce.product.domain.stock.QStock.stock;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    /** 판매 상태로 전체 조회한다. 재고를 조인해 한 번의 쿼리로 가져온다. */
    public List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses) {
        return queryFactory
                .select(projection())
                .from(product)
                .leftJoin(stock).on(product.id.eq(stock.productId))
                .where(statusIn(statuses))
                .fetch();
    }

    /**
     * 상품과 재고를 조인해 한 번의 쿼리로 조회한다.
     * 재고 행이 없는 상품도 조회되도록 left join 을 사용하며, 이때 재고는 0 으로 채운다.
     */
    public List<ProductInfo.Product> findAll(ProductCommand.Query command) {
        return queryFactory
                .select(projection())
                .from(product)
                .leftJoin(stock).on(product.id.eq(stock.productId))
                .where(
                        statusIn(command.getStatuses()),
                        idIn(command.getIds()),
                        cursorLt(command.getCursor())
                )
                .orderBy(product.id.desc())
                .limit(command.getPageSize())
                .fetch();
    }

    private ConstructorExpression<ProductInfo.Product> projection() {
        return Projections.constructor(
                ProductInfo.Product.class,
                product.id,
                product.name,
                product.price,
                stock.quantity.coalesce(0)
        );
    }

    private BooleanExpression statusIn(List<ProductSellingStatus> statuses) {
        return statuses == null || statuses.isEmpty() ? null : product.sellStatus.in(statuses);
    }

    private BooleanExpression idIn(List<Long> ids) {
        return ids == null || ids.isEmpty() ? null : product.id.in(ids);
    }

    private BooleanExpression cursorLt(Long cursor) {
        return cursor == null ? null : product.id.lt(cursor);
    }
}
