-- 부하 테스트용 더미 데이터 픽스처
-- 전제: 앱이 dev 프로파일(ddl-auto: create)로 기동되어 스키마가 이미 생성된 상태에서 실행한다.
--       테이블 스키마는 JPA가 만든 것을 그대로 쓰고, 이 스크립트는 데이터만 채운다(TRUNCATE + INSERT).

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;
SET SESSION cte_max_recursion_depth = 100000;

TRUNCATE TABLE stock;
TRUNCATE TABLE product;
TRUNCATE TABLE balance;
TRUNCATE TABLE balance_transaction;
TRUNCATE TABLE coupon;
TRUNCATE TABLE user_coupon;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_product;
TRUNCATE TABLE payment;
TRUNCATE TABLE `user`;

START TRANSACTION;

-- 상품 1만 건
INSERT INTO product (name, price, sell_status)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10000
)
SELECT CONCAT('상품명', n), 1000, 'SELLING' FROM seq;

-- 재고 1만 건 (상품과 1:1)
INSERT INTO stock (product_id, quantity)
SELECT product_id, 1000 FROM product;

-- 사용자 1만 건
INSERT INTO `user` (nickname)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10000
)
SELECT CONCAT('사용자', n) FROM seq;

-- 잔액 1만 건 (user_id 1~10000과 매칭, 낙관적 락 version 포함)
INSERT INTO balance (user_id, amount, version)
SELECT user_id, 1000000, 0 FROM `user`;

-- 쿠폰 (발급 가능 1건, 수량 10만)
INSERT INTO coupon (name, quantity, discount_rate, expired_at, status)
VALUES ('쿠폰명1', 100000, 0.3, DATE_ADD(NOW(), INTERVAL 7 DAY), 'PUBLISHABLE');

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
