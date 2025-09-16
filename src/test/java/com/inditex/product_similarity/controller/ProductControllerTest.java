package com.inditex.product_similarity.controller;

import com.inditex.dto.ProductDetail;
import com.inditex.product_similarity.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

@Test
void getProductSimilar_returnsProductDetails() {
    String productId = "123";

    ProductDetail detail1 = new ProductDetail();
    detail1.setId("A");
    detail1.setName("Shirt Blue");
    detail1.setPrice(BigDecimal.valueOf(49.99));
    detail1.setAvailability(true);

    ProductDetail detail2 = new ProductDetail();
    detail2.setId("B");
    detail2.setName("Shirt Red");
    detail2.setPrice(BigDecimal.valueOf(59.99));
    detail2.setAvailability(true);

    List<ProductDetail> mockDetails = List.of(detail1, detail2);

    when(productService.getSimilarProducts(productId)).thenReturn(mockDetails);

    // 👇 usa el tipo que devuelva realmente el controller (aquí Set si tu método lo firma así)
    ResponseEntity<Set<ProductDetail>> response = productController.getProductSimilar(productId);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertTrue(response.getBody().contains(detail1));
    assertTrue(response.getBody().contains(detail2));
    verify(productService, times(1)).getSimilarProducts(productId);
}



    @Test
    void getProductSimilar_returnsEmptyListWhenNoSimilarProducts() {
        String productId = "456";
        when(productService.getSimilarProducts(productId)).thenReturn(Collections.emptyList());

        ResponseEntity<Set<ProductDetail>> response = productController.getProductSimilar(productId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(productService, times(1)).getSimilarProducts(productId);
    }

    @Test
    void getProductSimilar_throwsException_whenServiceReturnsNull() {
        String productId = "789";
        when(productService.getSimilarProducts(productId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            productController.getProductSimilar(productId);
        });

        verify(productService, times(1)).getSimilarProducts(productId);
    }

}