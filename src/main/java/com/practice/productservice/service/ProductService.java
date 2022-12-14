package com.practice.productservice.service;

import com.practice.productservice.client.NotificationClient;
import com.practice.productservice.client.UserClient;
import com.practice.productservice.constant.Constant;
import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.SendEmailRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ListUserResponse;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.dto.UserDto;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.PostType;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.ProductType;
import com.practice.productservice.entity.UserProductRelation;
import com.practice.productservice.exception.BusinessException;
import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.exception.ProductNotFound;
import com.practice.productservice.repository.ProductRepository;
import com.practice.productservice.repository.UserProductRelationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final UserClient userClient;

    private final UserProductRelationRepository userProductRelationRepository;

    private final NotificationClient notificationClient;


    public CommonPageModel<ProductResponseForPage> list(Pageable pageable, ProductType productType) {
        if (Objects.isNull(productType)) {
            Page<Product> all = productRepository.findAll(pageable);
            return getCommonPageModel(pageable, all);
        }
        Page<Product> listByType = productRepository.findByProductType(productType, pageable);
        return getCommonPageModel(pageable, listByType);
    }

    public CommonPageModel<ProductResponseForPage> listFavoriteProducts(Pageable pageable, UserDto userDto) {
        List<Long> idList = userProductRelationRepository.findByUserId(userDto.getUserId())
                .stream()
                .map(UserProductRelation::getProductId)
                .collect(Collectors.toList());
        Page<Product> products = productRepository.findByIdIn(idList, pageable);
        return getCommonPageModel(pageable, products);
    }

    @Transactional
    public void remove(UserDto userDto, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.getUserId().equals(userDto.getUserId())) {
            throw new BusinessException(ErrorCode.PRODUCT_OWNER_EXCEPTION);
        }
        productRepository.deleteById(productId);
        userProductRelationRepository.deleteAllByProductId(productId);
    }

    public void add(UserDto userDto, AddProductRequest addProductRequest) {
        ListUserResponse user = userClient.getUserById(userDto.getUserId());
        if (Objects.nonNull(addProductRequest.getUrl())) {
            List<String> urls = addProductRequest.getUrl();
            List<Image> imageList = urls.stream().map(url -> Image.builder().url(url).build()).collect(Collectors.toList());
            productRepository.save(Product.buildProductFrom(user, addProductRequest, imageList, PostType.SELL));
        } else {
            Product product = Product.buildProductFrom(user, addProductRequest, Collections.emptyList(), PostType.BUY);
            productRepository.save(product);
        }
    }

    public void update(Long id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateProduct(updateProductRequest);
        productRepository.save(product);
    }

    public void favorite(UserDto userDto, Long productId) {
        Long userId = userDto.getUserId();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        if (userProductRelationRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_FAVORITE);
        }
        userProductRelationRepository.save(UserProductRelation.buildUserProductRelation(userId, product.getId()));
    }

    @Transactional
    public void removeFavorite(UserDto userDto, Long productId) {
        UserProductRelation relation = userProductRelationRepository
                .findByUserIdAndProductId(userDto.getUserId(), productId)
                .orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        userProductRelationRepository.deleteById(relation.getId());
    }

    public void sendNotification(UserDto userDto, Long productId) {
        Long userId = userDto.getUserId();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        ListUserResponse postOwner = userClient.getUserById(product.getUserId());
        if (!product.getUserId().equals(postOwner.getId())) {
            throw new BusinessException(ErrorCode.PRODUCT_OWNER_EXCEPTION);
        }
        ListUserResponse contactor = userClient.getUserById(userId);
        if (product.getPostType().equals(PostType.SELL)) {
            SendEmailRequest buyProductNotification = SendEmailRequest.buildNotificationRequestFrom(
                    postOwner, contactor.getEmail(), Constant.BUY_SUBJECT, Constant.BUY_EMAIL_BODY);
            notificationClient.sendEmail(buyProductNotification);
        } else if (product.getPostType().equals(PostType.BUY)) {
            SendEmailRequest sellProductNotification = SendEmailRequest.buildNotificationRequestFrom(
                    postOwner, contactor.getEmail(), Constant.SELL_SUBJECT, Constant.SELL_EMAIL_BODY);
            notificationClient.sendEmail(sellProductNotification);
        } else {
            throw new BusinessException(ErrorCode.UNKNOWN_POST_TYPE);
        }
    }

    private CommonPageModel<ProductResponseForPage> getCommonPageModel(Pageable pageable, Page<Product> products) {
        List<Long> userIdList = products.map(Product::getUserId).toList();
        List<ListUserResponse> users = userClient.getUsersByIdList(userIdList);
        List<ProductResponseForPage> responses = products.stream().
                map(product -> ProductResponseForPage.buildProductResponseForPageFrom(
                        product, users.stream().filter(user -> user.getId().equals(product.getUserId()))
                                .collect(Collectors.toList())
                                .get(0)))
                .collect(Collectors.toList());
        return CommonPageModel.from(products, pageable, responses);
    }

}
