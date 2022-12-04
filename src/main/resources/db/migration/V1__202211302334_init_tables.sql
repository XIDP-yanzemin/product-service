DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `images`;

CREATE TABLE `products` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(20, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL
);
create table products_image_info
(
    id         bigint auto_increment
        primary key,
    url        varchar(255) null,
    product_id bigint       null,
    constraint FK25ml0swc181fg53ln7tbkix8t
        foreign key (product_id) references products (id)
);
