DROP TABLE IF EXISTS `user_product_relation`;

CREATE TABLE `user_product_relation`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL
);
