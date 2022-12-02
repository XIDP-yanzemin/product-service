DROP TABLE IF EXISTS `products`;

CREATE TABLE `products` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(20, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL
)
