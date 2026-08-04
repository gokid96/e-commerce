-- 부하 테스트용 더미 데이터 픽스처
-- 전제: 앱이 dev 프로파일(ddl-auto: create)로 기동되어 스키마가 이미 생성된 상태에서 실행한다.
--       테이블 스키마는 JPA가 만든 것을 그대로 쓰고, 이 스크립트는 데이터만 채운다(TRUNCATE + INSERT).

-- 안전 모드 해제 (다량 삽입)
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- 테이블 초기화 (스키마 유지, 데이터만 제거)
TRUNCATE TABLE stock;
TRUNCATE TABLE product;
TRUNCATE TABLE balance;
TRUNCATE TABLE balance_transaction;
TRUNCATE TABLE coupon;
TRUNCATE TABLE user_coupon;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_product;
TRUNCATE TABLE payment;

-- 상품 + 재고 (1만 건)
DROP PROCEDURE IF EXISTS generate_product_stock_data;
DELIMITER //
CREATE PROCEDURE generate_product_stock_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE pid BIGINT;
  WHILE i <= 10000 DO
    INSERT INTO product (name, price, sell_status)
    VALUES (CONCAT('상품명', i), 1000, 'SELLING');

    SET pid = LAST_INSERT_ID();

    INSERT INTO stock (product_id, quantity)
    VALUES (pid, 1000);

    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;

-- 잔액 (1만 건, 낙관적 락 version 컬럼 포함)
DROP PROCEDURE IF EXISTS generate_balance_data;
DELIMITER //
CREATE PROCEDURE generate_balance_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 10000 DO
    INSERT INTO balance (user_id, amount, version)
    VALUES (i, 1000000, 0);

    SET i = i + 1;
  END WHILE;
END //
DELIMITER ;

-- 쿠폰 (발급 가능 1건, 수량 10만)
DROP PROCEDURE IF EXISTS generate_coupon_data;
DELIMITER //
CREATE PROCEDURE generate_coupon_data()
BEGIN
  INSERT INTO coupon (name, quantity, discount_rate, expired_at, status)
  VALUES ('쿠폰명1', 100000, 0.3, DATE_ADD(CURRENT_DATE(), INTERVAL 7 DAY), 'PUBLISHABLE');
END //
DELIMITER ;

-- 프로시저 실행
CALL generate_product_stock_data();
CALL generate_balance_data();
CALL generate_coupon_data();

-- 프로시저 정리
DROP PROCEDURE IF EXISTS generate_product_stock_data;
DROP PROCEDURE IF EXISTS generate_balance_data;
DROP PROCEDURE IF EXISTS generate_coupon_data;

COMMIT;

-- 안전 모드 복원
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
