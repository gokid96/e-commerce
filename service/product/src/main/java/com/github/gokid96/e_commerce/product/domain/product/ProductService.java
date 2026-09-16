package com.github.gokid96.e_commerce.product.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductInfo.Products getSellingProducts() {
        return ProductInfo.Products.of(productRepository.findBySellStatusIn(ProductSellingStatus.forSelling()));
    }

    /**
     * 커서 페이징 또는 ID 목록으로 상품을 조회한다.
     * ID 목록 조회일 때는 요청한 ID 순서를 보존하고, 조회되지 않은 ID 가 있으면 예외를 던진다.
     * (랭킹 조회처럼 순서가 결과의 의미인 호출이 있어 정렬을 호출자에게 맡기지 않는다.)
     */
    @Transactional(readOnly = true)
    public ProductInfo.Products getProducts(ProductCommand.Query command) {
        List<ProductInfo.Product> products = productRepository.findAll(command);

        if (command.getIds() == null || command.getIds().isEmpty()) {
            return ProductInfo.Products.of(products);
        }
        return ProductInfo.Products.of(sortByRequestedIds(products, command.getIds()));
    }

    @Transactional(readOnly = true)
    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts command) {
        List<Long> productIds = command.getProducts().stream()
                .map(ProductCommand.OrderProduct::getProductId)
                .toList();

        Map<Long, Product> products = productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return ProductInfo.OrderProducts.of(command.getProducts().stream()
                .map(item -> toOrderProduct(products, item))
                .toList());
    }

    private ProductInfo.OrderProduct toOrderProduct(Map<Long, Product> products, ProductCommand.OrderProduct item) {
        Product product = products.get(item.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        if (product.cannotSelling()) {
            throw new IllegalStateException("판매 중인 상품이 아닙니다.");
        }
        return ProductInfo.OrderProduct.of(
                product.getId(), product.getName(), product.getPrice(), item.getQuantity());
    }

    private List<ProductInfo.Product> sortByRequestedIds(List<ProductInfo.Product> products, List<Long> requestedIds) {
        Map<Long, ProductInfo.Product> byId = products.stream()
                .collect(Collectors.toMap(ProductInfo.Product::getProductId, Function.identity()));

        return new LinkedHashSet<>(requestedIds).stream()
                .map(id -> {
                    ProductInfo.Product product = byId.get(id);
                    if (product == null) {
                        throw new IllegalArgumentException("존재하지 않는 상품입니다.");
                    }
                    return product;
                })
                .toList();
    }
}
