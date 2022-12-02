package com.practice.productservice.controller;


import com.practice.productservice.WebApplicationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends WebApplicationTest {

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
            mockMvc.perform(delete("/products?id=1"))
                    .andExpect(status().isOk());
        }

        @Test
        void should_throw_404_error_when_product_not_exists() throws Exception {
            mockMvc.perform(delete("/products?id=100"))
                    .andExpect(status().isNotFound());
        }
    }

}
