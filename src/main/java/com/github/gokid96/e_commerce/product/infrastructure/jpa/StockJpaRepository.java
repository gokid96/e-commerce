package com.github.gokid96.e_commerce.product.infrastructure.jpa;

import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);
}
