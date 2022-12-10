package com.practice.productservice.service;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.client.UserFeignService;
import com.practice.productservice.constant.Constant;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.entity.UserProductRelation;
import com.practice.productservice.exception.BusinessException;
import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.exception.ProductNotFound;
import com.practice.productservice.repository.ImageRepository;
import com.practice.productservice.repository.ProductRepository;
import com.practice.productservice.repository.UserProductRelationRepository;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.BaseProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import com.practice.productservice.response.CommonPageModel;
import com.practice.productservice.response.ProductResponseForPage;
import com.practice.productservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private final UserFeignService userFeignService;
    private final JwtService jwtService;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private final UserProductRelationRepository userProductRelationRepository;


    public CommonPageModel<ProductResponseForPage> list(Pageable pageable, Type type) {
        if (Objects.isNull(type)) {
            Page<Product> all = productRepository.findAll(pageable);
            return getCommonPageModel(pageable, all);
        }
        Page<Product> listByType = productRepository.findByType(type, pageable);
        return getCommonPageModel(pageable, listByType);
    }

    public CommonPageModel<ProductResponseForPage> listFavoriteProducts(Pageable pageable, String token) {
        ListUserResponse user = userFeignService.getUserById(jwtService.decodeIdFromJwt(token));
        //token 里面不是有 id 嘛？为什么还需要再冲 user-service 里面获取 user 呢？
        List<Long> idList = userProductRelationRepository.findByUserId(user.getId())
                .stream()
                .map(UserProductRelation::getProductId)
                .collect(Collectors.toList());
        Page<Product> products = productRepository.findByIdIn(idList, pageable);
        return getCommonPageModel(pageable, products);
    }

    @Transactional
    public void remove(Long productId) {
        //todo 谁都可以 remove 嘛？ remove 之后其他用户如果收藏了该商品，会发生什么...
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteByProductId(productId);
        productRepository.deleteById(productId);
    }

    public ProductResponseForPage add(String token, AddProductRequest addProductRequest) {
        Long userId = jwtService.decodeIdFromJwt(token);
        ListUserResponse user = userFeignService.getUserById(userId);
        List<String> urls = addProductRequest.getUrls();

        Product product = productRepository.save(Product.buildProductFrom(user, addProductRequest));
        urls.stream().map(url -> Image.relateUrlToProduct(product, url)).forEach(imageRepository::save);
        //todo token 里有用户 id 不需要再查询一次了... 添加/更新/删除 是可以不需要返回值 response 的
        return ProductResponseForPage.from(user, urls, product);
    }

    public Product update(Long id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        //todo 自己更新自己为撒还需要把自己当作参数传进去？
        product.updateProductInfo(updateProductRequest, product);
        return productRepository.save(product);
    }

    private CommonPageModel<ProductResponseForPage> getCommonPageModel(Pageable pageable, Page<Product> products) {
        List<Long> idList = products.stream().map(Product::getId).collect(Collectors.toList());
        //todo 这如果是 1-n 是不是就不需要单独再查一次 image 了？
        List<Image> imageList = imageRepository.findByProductIdIn(idList);
        //todo 是不是可以使用 Stream.Map
        List<ProductResponseForPage> responses = new ArrayList<>();
        for (Product product : products) {
            //todo 不要再 for 中做 IO 操作 网络/DB ... 效率很低，考虑批量查询
            ListUserResponse user = userFeignService.getUserById(product.getUserId());

            ProductResponseForPage productResponse = ProductResponseForPage
                    .buildProductResponseForPageFrom(imageList, product, user);
            responses.add(productResponse);
        }
        return CommonPageModel.from(products, pageable, responses);
    }

    public void favorite(String token, Long id) {
        Long userId = jwtService.decodeIdFromJwt(token);
        //todo 如果已经添加收藏 还能继续添加嘛？
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        userProductRelationRepository.save(UserProductRelation.buildUserProductRelation(userId, product.getId()));
    }

    @Transactional
    public void removeFavorite(String token, Long productId) {
        Long userId = jwtService.decodeIdFromJwt(token);
        UserProductRelation relation = userProductRelationRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        userProductRelationRepository.deleteById(relation.getId());
    }

    public ProductResponseForPage addWantToBuyProduct(String token, BaseProductRequest baseProductRequest) {
        Long userId = jwtService.decodeIdFromJwt(token);
        ListUserResponse user = userFeignService.getUserById(userId);
        Product product = Product.buildProductFrom(baseProductRequest, userId);
        productRepository.save(product);
        return ProductResponseForPage.from(user, Collections.emptyList(), product);
    }

    public void buyProduct(String token, Long productId) {
        sendEmailToEmailReceiver(token, productId, Constant.BUY_SUBJECT, Constant.BUY_EMAIL_BODY);
    }


    public void sellProduct(String token, Long productId) {
        sendEmailToEmailReceiver(token, productId, Constant.SELL_SUBJECT, Constant.SELL_EMAIL_BODY);
    }

    private void sendEmailToEmailReceiver(String token, Long productId, String buySubject, String buyEmailBody) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        Long userId = jwtService.decodeIdFromJwt(token);
        if(!product.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.PRODUCT_OWNER_EXCEPTION);
        }
        ListUserResponse contactor = userFeignService.getUserById(userId);
        ListUserResponse emailReceiver = userFeignService.getUserById(product.getUserId());

        sendEmail(emailReceiver, buySubject, buyEmailBody, contactor);
    }

    private void sendEmail(ListUserResponse emailReceiver, String subject, String x, ListUserResponse contactor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(emailReceiver.getEmail());
        message.setSubject(subject);
        message.setText(x + contactor.getEmail());
        try {
            javaMailSender.send(message);
        } catch (MailSendException e) {
            throw new MailSendException("Fail to send email.");
        }
    }
}
