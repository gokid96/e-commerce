SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS balance, stock, user_coupon, payment, order_product, product;

CREATE TABLE balance (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT, amount BIGINT);
CREATE TABLE stock (stock_id BIGINT PRIMARY KEY AUTO_INCREMENT, product_id BIGINT, quantity INT);
CREATE TABLE user_coupon (user_coupon_id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT, coupon_id BIGINT, used_status VARCHAR(20), issued_at DATETIME, used_at DATETIME);
CREATE TABLE payment (payment_id BIGINT PRIMARY KEY AUTO_INCREMENT, order_id BIGINT, amount BIGINT, payment_method VARCHAR(20), payment_status VARCHAR(20), paid_at DATETIME);
CREATE TABLE order_product (order_product_id BIGINT PRIMARY KEY AUTO_INCREMENT, order_id BIGINT, product_id BIGINT, product_name VARCHAR(255), unit_price BIGINT, quantity INT);
CREATE TABLE product (product_id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), price BIGINT, sell_status VARCHAR(20));

DROP PROCEDURE IF EXISTS gen_data;
DELIMITER //
CREATE PROCEDURE gen_data()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 100000 DO
    INSERT INTO balance (user_id, amount) VALUES (i, 1000000);
INSERT INTO stock (product_id, quantity) VALUES (i, 1000);
INSERT INTO user_coupon (user_id, coupon_id, used_status, issued_at) VALUES (i, (i % 100) + 1, 'UNUSED', NOW());
INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_at) VALUES (i, 10000, 'UNKNOWN', 'COMPLETED', DATE_ADD('2024-01-01', INTERVAL (i % 365) DAY));
INSERT INTO product (name, price, sell_status) VALUES (CONCAT('product', i), 1000, IF(i % 10 = 0, 'STOP_SELLING', 'SELLING'));
INSERT INTO order_product (order_id, product_id, product_name, unit_price, quantity) VALUES ((i % 1000) + 1, i, 'product', 1000, 1);
SET i = i + 1;
END WHILE;
END //
DELIMITER ;

START TRANSACTION;
CALL gen_data();
COMMIT;

DROP PROCEDURE gen_data;
SET FOREIGN_KEY_CHECKS = 1;