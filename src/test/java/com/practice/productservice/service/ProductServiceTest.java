package com.practice.productservice.service;

import com.practice.productservice.client.UserClient;
import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ListUserResponse;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.dto.UserDto;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.ProductType;
import com.practice.productservice.entity.UserProductRelation;
import com.practice.productservice.exception.ProductNotFound;
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
    private UserClient userClient;

    @Mock
    private UserProductRelationRepository userProductRelationRepository;


    Image image = new Image();
    Product product = new Product();

    @BeforeEach
    void setUp() {
        image.setId(1L);
        image.setUrl("url");

        product.setId(1L);
        product.setUserId(1L);
        product.setProductName("product1");
        product.setPrice(new BigDecimal(1000));
        product.setDescription("");
        product.setAmount(1000);
        product.setProductType(ProductType.SPORTING_GOODS);
        product.setImageList(List.of(image));
    }

    @Nested
    class ListProductsTest {
        @BeforeEach
        void setUp() {
            List<ListUserResponse> listUserResponse = List.of(new ListUserResponse(1L, "user", "test@gmail.com", "1234567890", "test address"));
            when(userClient.getUsersByIdList(List.of(1L))).thenReturn(listUserResponse);
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
            verify(userClient, times(1)).getUsersByIdList(List.of(1L));
        }

        @Test
        void given_page_request_and_type_then_list_should_return_products_info() {
            Pageable page = PageRequest.of(0, 2);
            Page<Product> products = new PageImpl<>(List.of(product), page, List.of(product).size());

            when(productRepository.findByProductType(ProductType.ART, page)).thenReturn(products);

            CommonPageModel<ProductResponseForPage> response = productService.list(page, ProductType.ART);

            assertEquals(1, response.getNumberOfElements());
            assertEquals(0, response.getPageNumber());
            assertEquals(2, response.getPageSize());
            assertEquals(1, response.getContent().size());
            assertEquals(1L, response.getContent().get(0).getId());
            assertEquals(ProductType.SPORTING_GOODS, response.getContent().get(0).getProductType());

            verify(productRepository, times(1)).findByProductType(ProductType.ART, page);
            verify(userClient, times(1)).getUsersByIdList(List.of(1L));
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
    }

    @Nested
    class AddNewProductTest {
        UserDto userDto = new UserDto();

        @BeforeEach
        void setUp() {
            userDto.setUserId(1L);
        }

        @Test
        void given_add_product_request_then_add_should_save_product_info() {
            AddProductRequest addProductRequest = AddProductRequest.builder()
                    .name("testName")
                    .description("")
                    .price(new BigDecimal(10000))
                    .amount(1)
                    .productType(ProductType.ART)
                    .url(List.of("url"))
                    .build();
            ListUserResponse user = ListUserResponse.builder().id(1L).username("username").cellphone("1234567890").email("test@gmail.com").address("address").build();
            when(userClient.getUserById(1L)).thenReturn(user);
            when(productRepository.save(any(Product.class))).thenReturn(null);

            productService.add(userDto, addProductRequest);

            verify(userClient, times(1)).getUserById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        void given_base_product_request_then_add_should_save_product_info() {
            AddProductRequest addProductRequest = AddProductRequest.builder()
                    .name("testName")
                    .description("")
                    .price(new BigDecimal(10000))
                    .amount(1)
                    .productType(ProductType.ART)
                    .url(null)
                    .build();
            ListUserResponse user = ListUserResponse.builder().id(1L).username("username").cellphone("1234567890").email("test@gmail.com").address("address").build();
            when(userClient.getUserById(1L)).thenReturn(user);
            when(productRepository.save(any(Product.class))).thenReturn(null);

            productService.add(userDto, addProductRequest);

            verify(userClient, times(1)).getUserById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }
    }

    @Test
    void given_page_request_then_list_should_list_favorite_products() {
        UserDto userDto = new UserDto(1L);
        Pageable page = PageRequest.of(0, 2);
        Page<Product> products = new PageImpl<>(List.of(product), page, List.of(product).size());
        List<ListUserResponse> listUserResponse = List.of(new ListUserResponse(1L, "user", "test@gmail.com", "1234567890", "test address"));
        when(userClient.getUsersByIdList(List.of(1L))).thenReturn(listUserResponse);

        UserProductRelation userProductRelation = new UserProductRelation(1L, 1L, 1L);
        when(userProductRelationRepository.findByUserId(userDto.getUserId())).thenReturn(List.of(userProductRelation));
        when(productRepository.findByIdIn(List.of(userProductRelation.getProductId()), page)).thenReturn(products);

        CommonPageModel<ProductResponseForPage> responses = productService.listFavoriteProducts(page, userDto);

        assertEquals(responses.getPageNumber(), 0);
        assertEquals(responses.getPageSize(), 2);
        assertEquals(responses.getNumberOfElements(), 1);
        assertEquals(responses.getContent().get(0).getId(), product.getId());
        assertEquals(responses.getContent().get(0).getUserId(), userDto.getUserId());

        verify(userProductRelationRepository, times(1)).findByUserId(userDto.getUserId());
        verify(productRepository, times(1)).findByIdIn(List.of(userProductRelation.getProductId()), page);
    }


    @Test
    void given_update_product_request_then_update_should_update_product_info() {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("newName", "description", new BigDecimal(2000), 99999, ProductType.SPORTING_GOODS);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.update(1L, updateProductRequest);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void given_product_id_then_favorite_should_add_product_to_favorite() {
        UserDto userDto = new UserDto(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userProductRelationRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(userProductRelationRepository.save(any(UserProductRelation.class))).thenReturn(null);

        productService.favorite(userDto, 1L);

        verify(productRepository, times(1)).findById(1L);
        verify(userProductRelationRepository, times(1)).findByUserIdAndProductId(1L, 1L);
        verify(userProductRelationRepository, times(1)).save(any(UserProductRelation.class));
    }

    @Test
    void given_product_id_then_remove_favorite_should_remove_user_product_relation() {
        UserDto userDto = new UserDto(1L);
        UserProductRelation userProductRelation = new UserProductRelation(1L, 1L, 1L);
        when(userProductRelationRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.of(userProductRelation));
        doNothing().when(userProductRelationRepository).deleteById(1L);

        productService.removeFavorite(userDto, 1L);

        verify(userProductRelationRepository, times(1)).findByUserIdAndProductId(1L, 1L);
        verify(userProductRelationRepository, times(1)).deleteById(1L);
    }
}
