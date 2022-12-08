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
import com.practice.productservice.request.UpdateProductRequest;
import com.practice.productservice.response.CommonPageModel;
import com.practice.productservice.response.ProductResponseForPage;
import com.practice.productservice.response.UploadImageResponse;
import com.practice.productservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    private final UserFeignService userFeignService;
    private final JwtService jwtService;

    private final UserProductRelationRepository userProductRelationRepository;

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long maxSize;

    @Value("${file-save-path}")
    private String fileSavePath;

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
        List<Long> idList = userProductRelationRepository.findByUserId(user.getId())
                .stream()
                .map(UserProductRelation::getProductId)
                .collect(Collectors.toList());
        Page<Product> products = productRepository.findByIdIn(idList, pageable);
         return getCommonPageModel(pageable, products);
    }

    @Transactional
    public void remove(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteByProductId(productId);
        productRepository.deleteById(productId);
    }

    public UploadImageResponse upload(HttpServletRequest request, MultipartFile[] files) throws BusinessException {
        List<String> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.IMAGE_EMPTY_EXCEPTION);
            }
            System.out.println(file.getSize());
            if (file.getSize() > maxSize) {
                throw new BusinessException(ErrorCode.IMAGE_SIZE_EXCEPTION);
            }
            if (!Constant.IMAGE_TYPES.contains(file.getContentType())) {
                throw new BusinessException(ErrorCode.IMAGE_TYPE_EXCEPTION);
            }

            File dir = new File(fileSavePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            log.info("图片上传，保存位置：" + fileSavePath);

            String suffix = Objects.requireNonNull(file.getOriginalFilename()).trim().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            File dest = new File(dir, newFileName);
            try {
                file.transferTo(dest);
            } catch (IllegalStateException e) {
                throw new BusinessException(ErrorCode.IMAGE_STATE_EXCEPTION);
            } catch (IOException e) {
                log.info(e.getMessage());
                throw new BusinessException(ErrorCode.IMAGE_UPLOAD_EXCEPTION);
            }
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/images/" + newFileName;
            responses.add(url);
        }

        return new UploadImageResponse(responses);
    }

    public ProductResponseForPage add(String token, AddProductRequest addProductRequest) {
        Long userId = jwtService.decodeIdFromJwt(token);
        ListUserResponse user = userFeignService.getUserById(userId);
        List<String> urls = addProductRequest.getUrls();

        Product product = productRepository.save(Product.buildProductFrom(user, addProductRequest));
        urls.stream().map(url -> Image.relateUrlToProduct(product, url)).forEach(imageRepository::save);

        return ProductResponseForPage.from(userId, user, urls, product);
    }

    public Product update(Long id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        product.updateProductInfo(updateProductRequest, product);
        return productRepository.save(product);
    }

    private CommonPageModel<ProductResponseForPage> getCommonPageModel(Pageable pageable, Page<Product> products) {
        List<Long> idList = products.stream().map(Product::getId).collect(Collectors.toList());
        List<Image> imageList = imageRepository.findByProductIdIn(idList);

        List<ProductResponseForPage> responses = getProductResponseForPages(products, imageList);

        return CommonPageModel.from(pageable, responses);
    }

    private List<ProductResponseForPage> getProductResponseForPages(Page<Product> products, List<Image> imageList) {
        List<ProductResponseForPage> responses = new ArrayList<>();
        for (Product product : products) {
            ListUserResponse user = userFeignService.getUserById(product.getUserId());

            ProductResponseForPage productResponse = ProductResponseForPage
                    .buildProductResponseForPageFrom(imageList, product, user);
            responses.add(productResponse);
        }
        return responses;
    }

    public void favorite(String token, Long id) {
        Long userId = jwtService.decodeIdFromJwt(token);
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        userProductRelationRepository.save(UserProductRelation.buildUserProductRelation(userId, product.getId()));
    }

    public void removeFavorite(String token, Long productId) {
        Long userId = jwtService.decodeIdFromJwt(token);
        UserProductRelation relation = userProductRelationRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ProductNotFound(ErrorCode.PRODUCT_NOT_FOUND));
        userProductRelationRepository.deleteById(relation.getId());
    }

}
