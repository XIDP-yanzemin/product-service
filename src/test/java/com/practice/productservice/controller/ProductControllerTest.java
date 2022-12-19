package com.practice.productservice.controller;


import com.practice.productservice.WebApplicationTest;
import com.practice.productservice.client.NotificationFeignService;
import com.practice.productservice.constant.Constant;
import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.BaseProductRequest;
import com.practice.productservice.controller.request.SendEmailRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.entity.ProductType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class
ProductControllerTest extends WebApplicationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NotificationFeignService notificationFeignService;

    @Nested
    class ListProductsTest {

        @Test
        @Sql("/sql/data.sql")
        void should_return_all_products_by_page() throws Exception {
            Pageable page = PageRequest.of(0, 2);

            mockMvc.perform(get("/product-service/api/v1/products?page=0&size=2")
                            .header("token", Constant.TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(page)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(2))
                    .andExpect(jsonPath("$.numberOfElements").value(4))
                    .andReturn()
                    .getResponse();
        }

        @Test
        @Sql("/sql/data.sql")
        void should_return_favorite_products_by_page() throws Exception {
            Pageable page = PageRequest.of(0, 2);

            mockMvc.perform(get("/product-service/api/v1/products/favorites")
                            .header("token", Constant.TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(page)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(10))
                    .andExpect(jsonPath("$.numberOfElements").value(1))
                    .andReturn()
                    .getResponse();
        }

        @Test
        @Sql("/sql/data.sql")
        void should_return_product_by_page_and_type() throws Exception {
            Pageable page = PageRequest.of(0, 2);

            mockMvc.perform(get("/product-service/api/v1/products?page=0&size=2&type=BEAUTY")
                            .header("token", Constant.TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(page))
                            .contentType(OBJECT_MAPPER.writeValueAsString("BEAUTY")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(2))
                    .andExpect(jsonPath("$.numberOfElements").value(1))
                    .andReturn()
                    .getResponse();
        }
    }

    @Nested
    class RemoveProductTest {
        @Test
        @Sql("/sql/data.sql")
        void should_remove_product_by_id() throws Exception {
            mockMvc.perform(delete("/product-service/api/v1/products?id=3")
                            .header("token", Constant.TOKEN))
                    .andExpect(status().isNoContent());
        }

        @Test
        void should_throw_404_error_when_product_not_exists() throws Exception {
            mockMvc.perform(delete("/product-service/api/v1/products?id=100")
                            .header("token", Constant.TOKEN))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void should_add_new_product_info() throws Exception {
        AddProductRequest addProductRequest = AddProductRequest.builder()
                .name("testName")
                .description("")
                .price(new BigDecimal(1000))
                .amount(1000)
                .productType(ProductType.SPORTING_GOODS)
                .url(List.of("url"))
                .build();
        mockMvc.perform(post("/product-service/api/v1/products")
                        .header("token", Constant.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(addProductRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/sql/data.sql")
    void should_update_product_info() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("testName", "new description", new BigDecimal(88888), 66666, ProductType.SPORTING_GOODS);
        mockMvc.perform(put("/product-service/api/v1/products/3")
                        .header("token", Constant.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateProductRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void should_throw_404_error_when_product_not_exists() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("testName", "new description", new BigDecimal(88888), 66666, ProductType.SPORTING_GOODS);
        mockMvc.perform(put("/product-service/api/v1/products/100000")
                        .header("token", Constant.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateProductRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("product not exists."));
    }

    @Test
    @Sql("/sql/data.sql")
    void should_add_product_to_favorite() throws Exception {
        mockMvc.perform(post("/product-service/api/v1/products/favorites?id=2")
                        .header("token", Constant.TOKEN))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/sql/data.sql")
    void should_remove_product_from_favorite() throws Exception {
        mockMvc.perform(delete("/product-service/api/v1/products/favorites?id=1")
                        .header("token", Constant.TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_add_want_to_buy_product() throws Exception {
        BaseProductRequest baseProductRequest = new BaseProductRequest(
                "want-to-buy product", "want to buy", new BigDecimal(666), 1, ProductType.BEAUTY);
        mockMvc.perform(post("/product-service/api/v1/products")
                        .header("token", Constant.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(baseProductRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/sql/data.sql")
    public void should_send_out_buy_item_email() throws Exception {
        doNothing().when(notificationFeignService).sendEmail(Mockito.any(SendEmailRequest.class));
        mockMvc.perform(post("/product-service/api/v1/products/notification/1")
                        .header("token", Constant.TOKEN))
                .andExpect(status().isOk());
    }
}
