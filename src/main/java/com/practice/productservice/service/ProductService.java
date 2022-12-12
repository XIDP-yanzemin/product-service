package com.practice.productservice.service;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.client.UserFeignService;
import com.practice.productservice.constant.Constant;
import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.dto.UserDto;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.entity.UserProductRelation;
import com.practice.productservice.exception.BusinessException;
import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.exception.ProductNotFound;
import com.practice.productservice.repository.ProductRepository;
import com.practice.productservice.repository.UserProductRelationRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final UserFeignService userFeignService;

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
        ListUserResponse user = userFeignService.getUserById(userDto.getUserId());
        if (Objects.nonNull(addProductRequest.getUrl())) {
            List<String> urls = addProductRequest.getUrl();
            List<Image> imageList = urls.stream().map(url -> Image.builder().url(url).build()).collect(Collectors.toList());
            productRepository.save(Product.buildProductFrom(user, addProductRequest, imageList));
        } else {
            Product product = Product.buildProductFrom(user, addProductRequest, Collections.emptyList());
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

    public void buyProduct(UserDto userDto, Long productId) {
        sendEmailToEmailReceiver(userDto, productId, Constant.BUY_SUBJECT, Constant.BUY_EMAIL_BODY);
    }


    public void sellProduct(UserDto userDto, Long productId) {
        sendEmailToEmailReceiver(userDto, productId, Constant.SELL_SUBJECT, Constant.SELL_EMAIL_BODY);
    }


    private CommonPageModel<ProductResponseForPage> getCommonPageModel(Pageable pageable, Page<Product> products) {
        List<Long> userIdList = products.map(Product::getUserId).toList();
        List<ListUserResponse> users = userFeignService.getUsersByIdList(userIdList);
        List<ProductResponseForPage> responses = products.stream().
                map(product -> ProductResponseForPage.buildProductResponseForPageFrom(
                        product, users.stream().filter(user -> user.getId().equals(product.getUserId()))
                                .collect(Collectors.toList())
                                .get(0)))
                .collect(Collectors.toList());
        return CommonPageModel.from(products, pageable, responses);
    }

    private void sendEmailToEmailReceiver(UserDto userDto, Long productId, String buySubject, String buyEmailBody) {
        Long userId = userDto.getUserId();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PRODUCT_OWNER_EXCEPTION);
        }
        ListUserResponse contactor = userFeignService.getUserById(userId);
        ListUserResponse emailReceiver = userFeignService.getUserById(product.getUserId());

        sendEmail(emailReceiver, buySubject, buyEmailBody, contactor);
    }

    private void sendEmail(ListUserResponse emailReceiver, String subject, String text, ListUserResponse contactor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(emailReceiver.getEmail());
        message.setSubject(subject);
        message.setText(text + contactor.getEmail());
        try {
            javaMailSender.send(message);
        } catch (MailSendException e) {
            throw new MailSendException("Fail to send email.");
        }
    }
}
