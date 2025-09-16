package com.inditex.product_similarity.mapper;

import com.inditex.product_similarity.model.Product;
import com.inditex.dto.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void testToDto_mapsFieldsCorrectly() {
        Product product = new Product();
        product.setId("1");
        product.setName("Test Product");
        product.setPrice(19.99);

        ProductDetail dto = productMapper.toDto(product);

        assertNotNull(dto);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(BigDecimal.valueOf(product.getPrice()), dto.getPrice());
    }

    @Test
    void testToDto_nullProductReturnsNull() {
        assertNull(productMapper.toDto(null));
    }

    @Test
    void testMapDoubleToBigDecimal() {
        assertEquals(BigDecimal.valueOf(10.5), productMapper.mapDoubleToBigDecimal(10.5));
        assertNull(productMapper.mapDoubleToBigDecimal(null));
    }

    @Test
    void testToDtoList_mapsListCorrectly() {
        Product p1 = new Product();
        p1.setId("1");
        p1.setName("A");
        p1.setPrice(1.0);

        Product p2 = new Product();
        p2.setId("2");
        p2.setName("B");
        p2.setPrice(2.0);

        List<Product> products = Arrays.asList(p1, p2);
        List<ProductDetail> dtos = productMapper.toDtoList(products);

        assertEquals(2, dtos.size());
        assertEquals(BigDecimal.valueOf(1.0), dtos.get(0).getPrice());
        assertEquals(BigDecimal.valueOf(2.0), dtos.get(1).getPrice());
    }

    @Test
    void testToDtoSet_mapsSetCorrectly() {
        Product p1 = new Product();
        p1.setId("1");
        p1.setName("A");
        p1.setPrice(1.0);

        Product p2 = new Product();
        p2.setId("2");
        p2.setName("B");
        p2.setPrice(2.0);

        List<Product> products = Arrays.asList(p1, p2);
        Set<ProductDetail> dtos = productMapper.toDtoSet(products);

        assertEquals(2, dtos.size());
        assertTrue(dtos.stream().anyMatch(dto -> dto.getId().equals("1")));
        assertTrue(dtos.stream().anyMatch(dto -> dto.getId().equals("2")));
    }
}