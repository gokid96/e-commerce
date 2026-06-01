package com.github.gokid96.e_commerce.product.infrastructure.jpa;

import com.github.gokid96.e_commerce.product.domain.product.Product;
import com.github.gokid96.e_commerce.product.domain.product.ProductSellingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellStatusIn(List<ProductSellingStatus> statuses);

}
