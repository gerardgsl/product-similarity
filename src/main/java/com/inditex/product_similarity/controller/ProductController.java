package com.inditex.product_similarity.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.inditex.api.ProductApi;
import com.inditex.dto.ProductDetail;
import com.inditex.product_similarity.service.ProductService;

@RestController
public class ProductController implements ProductApi {

    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @Override
    public ResponseEntity<Set<ProductDetail>> getProductSimilar(String productId) {
        Set<ProductDetail> details = new HashSet<>(productService.getSimilarProducts(productId));
        return ResponseEntity.ok(details);
    }
}