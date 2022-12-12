package com.practice.productservice.service;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.client.UserFeignService;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.dto.UserDto;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.exception.ProductNotFound;
import com.practice.productservice.interceptor.FeignInterceptor;
import com.practice.productservice.repository.ProductRepository;
import com.practice.productservice.repository.UserProductRelationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private FeignInterceptor feignInterceptor;

    @Mock
    private UserFeignService userFeignService;

    @Mock
    private UserProductRelationRepository userProductRelationRepository;


    Product product = new Product();

    @BeforeEach
    void setUp() {
        Image image = Image.builder().id(1L).url("url").build();
        product.setId(1L);
        product.setUserId(1L);
        product.setProductName("product1");
        product.setPrice(new BigDecimal(1000));
        product.setDescription("");
        product.setAmount(1000);
        product.setType(Type.SPORTING_GOODS);
        product.setImageList(List.of(image));
    }

    @Nested
    class ListProductsTest {
        @BeforeEach
        void setUp() {
            List<ListUserResponse> listUserResponse = List.of(new ListUserResponse(1L, "user", "test@gmail.com", "1234567890", "test address"));
            when(userFeignService.getUsersByIdList(List.of(1L))).thenReturn(listUserResponse);
        }

        @Test
        void given_page_number_and_size_then_list_should_return_products_by_page() {
            Pageable page = PageRequest.of(0, 2);

            Page<Product> products = new PageImpl<>(List.of(product), page, List.of(product).size());
            when(productRepository.findAll(page)).thenReturn(products);

            CommonPageModel<ProductResponseForPage> response = productService.list(page, null);

            assertEquals(1, response.getNumberOfElements());
            assertEquals(0, response.getPageNumber());
            assertEquals(2, response.getPageSize());
            assertEquals(1, response.getContent().size());
            assertEquals(1L, response.getContent().get(0).getId());

            verify(productRepository, times(1)).findAll(page);
            verify(userFeignService, times(1)).getUsersByIdList(List.of(1L));
        }

        @Test
        void given_page_request_and_type_then_list_should_return_products_info() {
            Pageable page = PageRequest.of(0, 2);
            Page<Product> products = new PageImpl<>(List.of(product), page, List.of(product).size());

            when(productRepository.findByType(Type.ART, page)).thenReturn(products);

            CommonPageModel<ProductResponseForPage> response = productService.list(page, Type.ART);

            assertEquals(1, response.getNumberOfElements());
            assertEquals(0, response.getPageNumber());
            assertEquals(2, response.getPageSize());
            assertEquals(1, response.getContent().size());
            assertEquals(1L, response.getContent().get(0).getId());
            assertEquals(Type.SPORTING_GOODS, response.getContent().get(0).getType());

            verify(productRepository, times(1)).findByType(Type.ART, page);
            verify(userFeignService, times(1)).getUsersByIdList(List.of(1L));
        }

    }

    @Nested
    class DeleteProductTest {

        UserDto userDto = new UserDto();
        @BeforeEach
        void setUp() {
            userDto.setUserId(1L);
        }

        @Test
        void given_product_id_then_remove_should_delete_product() {
            Long productId = 1L;
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).deleteById(productId);
            doNothing().when(userProductRelationRepository).deleteAllByProductId(productId);

            productService.remove(userDto, productId);

            verify(productRepository, times(1)).findById(productId);
            verify(productRepository, times(1)).deleteById(productId);
            verify(userProductRelationRepository, times(1)).deleteAllByProductId(productId);
        }

        @Test
        void given_not_existed_id_then_remove_should_throw_exception() {
            Long nonexistentProductId = 100L;

            when(productRepository.findById(nonexistentProductId)).thenReturn(Optional.empty());

            ProductNotFound exception = assertThrows(ProductNotFound.class, () -> productService.remove(userDto, nonexistentProductId));
            String message = exception.getMessage();

            assertTrue(message.contains("product not exists."));
            verify(productRepository, times(1)).findById(nonexistentProductId);
            verify(productRepository, times(0)).deleteById(nonexistentProductId);
            verify(userProductRelationRepository, times(0)).deleteAllByProductId(nonexistentProductId);
        }

//    @Test
//    void given_add_product_request_then_add_should_save_product_info() {
//        AddProductRequest addProductRequest = new AddProductRequest("testName", "", new BigDecimal(1000), 1000, Type.SPORTING_GOODS, List.of("url"));
//        ListUserResponse user = ListUserResponse.builder().id(1L).username("username").cellphone("1234567890").email("test@gmail.com").address("address").build();
//        Product product = Product.buildProductFrom(user, addProductRequest);
////        when(jwtService.decodeIdFromJwt("token")).thenReturn(1L);
////        when(userFeignService.getUserById(1L)).thenReturn(user);
//        when(productRepository.save(Product.buildProductFrom(user, addProductRequest))).thenReturn(product);
//        when(imageRepository.save(any(Image.class))).thenReturn(null);
//
//        productService.add(any(String.class), addProductRequest);
//
//        verify(productRepository, times(1)).save(any(Product.class));
//        verify(imageRepository, times(1)).save(any(Image.class));
//    }

        @Test
        void given_update_product_request_then_update_should_update_product_info() {
            Image image = new Image(1L, "url");
            Product product = new Product(1L, 1L, "product1", new BigDecimal(1000), "", 1000, Type.SPORTING_GOODS, List.of(image));

            UpdateProductRequest updateProductRequest = new UpdateProductRequest("newName", "description", new BigDecimal(2000), 99999, Type.SPORTING_GOODS);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.update(1L, updateProductRequest);

            verify(productRepository, times(1)).save(any(Product.class));
        }
    }
}
