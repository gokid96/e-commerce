package com.github.gokid96.e_commerce.product.infrastructure;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductCommand;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.gokid96.e_commerce.product.domain.product.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<Product> findAll(ProductCommand.Query command) {
        return queryFactory
                .selectFrom(product)
                .where(
                        product.sellStatus.in(command.getStatus()),
                        cursorLt(command.getCursor())
                )
                .orderBy(product.id.desc())
                .limit(command.getPageSize())
                .fetch();
    }

    private BooleanExpression cursorLt(Long cursor) {
        return cursor == null ? null : product.id.lt(cursor);
    }
}