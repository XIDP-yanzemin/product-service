package com.practice.productservice.controller;


import com.practice.productservice.WebApplicationTest;
import com.practice.productservice.entity.Type;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends WebApplicationTest {

    public static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiT1JESU5BUllfVVNFUiIsImlkIjoyLCJleHAiOjE2NzExMTM2NDd9.3WwL4gcCbhwGrV38AdtkPCjcpV3wJpsNGTLYQvbB3Dk";
    @Autowired
    private MockMvc mockMvc;

    @Nested
    class ListProductsTest {

        @Test
        @Sql("/sql/data.sql")
        void should_return_all_products_by_page() throws Exception {
            Pageable page = PageRequest.of(0, 2);

            mockMvc.perform(get("/products?page=0&size=2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(OBJECT_MAPPER.writeValueAsString(page)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageSize").value(2))
                    .andExpect(jsonPath("$.numberOfElements").value(2))
                    .andReturn()
                    .getResponse();
        }

        @Test
        @Sql("/sql/data.sql")
        void should_return_product_by_page_and_type() throws Exception {
            Pageable page = PageRequest.of(0, 2);

            mockMvc.perform(get("/products?page=0&size=2&type=BEAUTY")
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
            mockMvc.perform(delete("/products/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        void should_throw_404_error_when_product_not_exists() throws Exception {
            mockMvc.perform(delete("/products/100"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @Disabled
    void should_add_new_product_info() throws Exception {
        AddProductRequest addProductRequest = new AddProductRequest("testName", "", new BigDecimal(1000), 1000, Type.SPORTING_GOODS, List.of("url"));
        mockMvc.perform(post("/products")
                        .header("token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(addProductRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql("/sql/data.sql")
    void should_update_product_info() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("testName", "new description", new BigDecimal(88888), 66666, Type.SPORTING_GOODS);
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateProductRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productName").value("testName"))
                .andExpect(jsonPath("$.price").value(88888))
                .andExpect(jsonPath("$.type").value(Type.SPORTING_GOODS.toString()));
    }

    @Test
    void should_throw_404_error_when_product_not_exists() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest("testName", "new description", new BigDecimal(88888), 66666, Type.SPORTING_GOODS);
        mockMvc.perform(put("/products/100000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateProductRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("product not exists."));
    }
}
