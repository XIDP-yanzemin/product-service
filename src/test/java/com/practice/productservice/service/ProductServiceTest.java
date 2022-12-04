package com.practice.productservice.service;

import com.practice.productservice.response.ListProductsResponse;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.exception.ProductNotFound;
import com.practice.productservice.repository.ProductRepository;
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


    @Test
    void given_page_number_and_size_then_list_should_return_products_by_page() {
        Product product = new Product(1L, "test1", new BigDecimal(10), "description", 10, Type.BEAUTY);
        Pageable page = PageRequest.of(0, 2);

        Page<Product> products = new PageImpl<>(List.of(product), page, List.of(product).size());
        when(productRepository.findAll(page)).thenReturn(products);

        ListProductsResponse response = productService.listProductByRequest(page, null);

        assertEquals(1, response.getNumberOfElements());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(1, response.getContent().size());
        assertEquals(1L, response.getContent().get(0).getId());

        verify(productRepository, times(1)).findAll(page);
    }

    @Nested
    class DeleteProductTest {

        @Test
        void given_product_id_then_remove_should_delete_product() {
            Long id = 1L;
            Product product = new Product(1L, "test1", new BigDecimal(10), "description", 10, Type.BEAUTY);
            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).deleteById(id);

            productService.delete(id);

            verify(productRepository, times(1)).findById(id);
            verify(productRepository, times(1)).deleteById(id);
        }

        @Test
        void given_not_existed_id_then_remove_should_throw_exception() {
            Long id = 100L;

            when(productRepository.findById(id)).thenReturn(Optional.empty());

            ProductNotFound exception = assertThrows(ProductNotFound.class, () -> productService.delete(id));
            String message = exception.getMessage();

            assertTrue(message.contains("product not exists."));
            verify(productRepository, times(1)).findById(id);
            verify(productRepository, times(0)).deleteById(id);
        }
    }
}
