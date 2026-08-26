package com.github.gokid96.e_commerce.product.infrastructure.jpa;

import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findWithLockByProductId(Long productId);
}
