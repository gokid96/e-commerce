package com.github.gokid96.e_commerce.product.domain.product;

import com.github.gokid96.e_commerce.product.domain.stock.Stock;
import com.github.gokid96.e_commerce.product.domain.stock.StockRepository;
import com.github.gokid96.e_commerce.product.support.IntegrationTestSupport;
import com.github.gokid96.e_commerce.product.support.database.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * 상품 조회는 QueryDSL 로 stock 을 조인해 한 번의 쿼리로 처리한다.
 * 조인·프로젝션·정렬은 Mock 리포지토리로는 검증할 수 없어 통합 테스트로 고정한다.
 */
class ProductServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
    }

    @DisplayName("판매 중인 상품만 재고와 함께 조회한다.")
    @Test
    void getSellingProducts() {
        Product selling = productRepository.save(Product.create("판매중", 1_000L, ProductSellingStatus.SELLING));
        Product stopped = productRepository.save(Product.create("판매중지", 2_000L, ProductSellingStatus.STOP_SELLING));
        stockRepository.save(Stock.create(selling.getId(), 7));
        stockRepository.save(Stock.create(stopped.getId(), 5));

        ProductInfo.Products result = productService.getSellingProducts();

        assertThat(result.getProducts())
                .extracting("productId", "productName", "stockQuantity")
                .containsExactly(tuple(selling.getId(), "판매중", 7));
    }

    @DisplayName("재고 행이 없는 상품은 재고 0으로 조회된다.")
    @Test
    void getSellingProductsWhenStockMissing() {
        Product product = productRepository.save(Product.create("재고없음", 1_000L, ProductSellingStatus.SELLING));

        ProductInfo.Products result = productService.getSellingProducts();

        // left join + coalesce 가 빠지면 이 상품이 결과에서 아예 누락된다.
        assertThat(result.getProducts())
                .extracting("productId", "stockQuantity")
                .containsExactly(tuple(product.getId(), 0));
    }

    @DisplayName("ID 목록으로 조회하면 요청한 순서를 보존한다.")
    @Test
    void getProductsByIdsPreservesRequestedOrder() {
        Product first = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        Product second = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));
        Product third = productRepository.save(Product.create("상품3", 3_000L, ProductSellingStatus.SELLING));
        stockRepository.save(Stock.create(first.getId(), 1));
        stockRepository.save(Stock.create(second.getId(), 2));
        stockRepository.save(Stock.create(third.getId(), 3));

        List<Long> requested = List.of(second.getId(), third.getId(), first.getId());
        ProductInfo.Products result = productService.getProducts(ProductCommand.Query.ofIds(requested));

        // 조인 쿼리는 id desc 로 정렬되므로, 요청 순서 보존은 서비스가 책임진다.
        assertThat(result.getProducts())
                .extracting(ProductInfo.Product::getProductId)
                .containsExactlyElementsOf(requested);
    }

    @DisplayName("ID 목록 조회는 판매 상태로 걸러내지 않는다.")
    @Test
    void getProductsByIdsIgnoresSellingStatus() {
        Product stopped = productRepository.save(Product.create("판매중지", 2_000L, ProductSellingStatus.STOP_SELLING));
        stockRepository.save(Stock.create(stopped.getId(), 5));

        ProductInfo.Products result = productService.getProducts(
                ProductCommand.Query.ofIds(List.of(stopped.getId())));

        // 주문 경로에서 판매 중지 상품이 조용히 누락되면 주문 금액이 어긋난다.
        assertThat(result.getProducts())
                .extracting("productId", "stockQuantity")
                .containsExactly(tuple(stopped.getId(), 5));
    }

    @DisplayName("ID 목록에 존재하지 않는 상품이 있으면 조회에 실패한다.")
    @Test
    void getProductsByIdsWhenNotFound() {
        Product product = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));

        ProductCommand.Query command = ProductCommand.Query.ofIds(List.of(product.getId(), 999L));

        assertThatThrownBy(() -> productService.getProducts(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 상품입니다.");
    }

    @DisplayName("커서 페이징으로 판매 중인 상품을 조회한다.")
    @Test
    void getProductsByCursor() {
        Product first = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        Product second = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));
        Product third = productRepository.save(Product.create("상품3", 3_000L, ProductSellingStatus.SELLING));

        ProductInfo.Products result = productService.getProducts(
                ProductCommand.Query.ofSelling(2L, third.getId()));

        // cursor 미만, id desc, limit 2
        assertThat(result.getProducts())
                .extracting(ProductInfo.Product::getProductId)
                .containsExactly(second.getId(), first.getId());
    }

    @DisplayName("주문 상품을 수량과 함께 조회한다.")
    @Test
    void getOrderProducts() {
        Product first = productRepository.save(Product.create("상품1", 1_000L, ProductSellingStatus.SELLING));
        Product second = productRepository.save(Product.create("상품2", 2_000L, ProductSellingStatus.SELLING));

        ProductInfo.OrderProducts result = productService.getOrderProducts(
                ProductCommand.OrderProducts.of(List.of(
                        ProductCommand.OrderProduct.of(first.getId(), 2),
                        ProductCommand.OrderProduct.of(second.getId(), 3)
                )));

        assertThat(result.getProducts())
                .extracting("productId", "productPrice", "quantity")
                .containsExactly(
                        tuple(first.getId(), 1_000L, 2),
                        tuple(second.getId(), 2_000L, 3)
                );
    }

    @DisplayName("판매 중이 아닌 상품은 주문할 수 없다.")
    @Test
    void getOrderProductsWhenNotSelling() {
        Product stopped = productRepository.save(Product.create("판매중지", 1_000L, ProductSellingStatus.STOP_SELLING));

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(List.of(
                ProductCommand.OrderProduct.of(stopped.getId(), 1)
        ));

        assertThatThrownBy(() -> productService.getOrderProducts(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("판매 중인 상품이 아닙니다.");
    }
}
