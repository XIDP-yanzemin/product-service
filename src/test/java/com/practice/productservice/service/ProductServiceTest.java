package com.practice.productservice.service;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.client.UserFeignService;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.interceptor.FeignInterceptor;
import com.practice.productservice.repository.ProductRepository;
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
import static org.mockito.ArgumentMatchers.any;
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
    class ListProductsTest{
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

        @Test
        void given_product_id_then_remove_should_delete_product() {
            Long id = 1L;
            Product product = new Product(1L, 1L,"test1", new BigDecimal(10), "description", 10, Type.BEAUTY);
            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            doNothing().when(imageRepository).deleteByProductId(id);
            doNothing().when(productRepository).deleteById(id);

            productService.remove(id);

            verify(productRepository, times(1)).findById(id);
            verify(productRepository, times(1)).deleteById(id);
            verify(imageRepository, times(1)).deleteByProductId(id);
        }

        @Test
        void given_not_existed_id_then_remove_should_throw_exception() {
            Long id = 100L;

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            ProductNotFound exception = assertThrows(ProductNotFound.class, () -> productService.remove(id));
            String message = exception.getMessage();

            assertTrue(message.contains("product not exists."));
            verify(productRepository, times(1)).findById(id);
            verify(productRepository, times(0)).deleteById(id);
            verify(imageRepository, times(0)).deleteByProductId(id);
        }
    }

    @Test
    void given_add_product_request_then_add_should_save_product_info() {
        AddProductRequest addProductRequest = new AddProductRequest("testName", "", new BigDecimal(1000), 1000, Type.SPORTING_GOODS, List.of("url"));
        Product product = Product.buildProductFrom(addProductRequest);

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(imageRepository.save(any(Image.class))).thenReturn(null);

        productService.add(addProductRequest);

        verify(productRepository, times(1)).save(any(Product.class));
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void given_update_product_request_then_update_should_update_product_info() {
        Product product = new Product(1L, 1L,"product1", new BigDecimal(1000), "", 1000, Type.SPORTING_GOODS);

        UpdateProductRequest updateProductRequest = new UpdateProductRequest("newName", "description", new BigDecimal(2000), 99999, Type.SPORTING_GOODS);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.update(1L, updateProductRequest);

        verify(productRepository, times(1)).save(any(Product.class));
    }
}
