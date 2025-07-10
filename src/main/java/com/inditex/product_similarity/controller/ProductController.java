package com.inditex.product_similarity.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inditex.product_similarity.model.Product;
import com.inditex.product_similarity.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/{productId}/similar")
    public List<Product> getSimilar(@PathVariable String productId) {
        return service.getSimilarProducts(productId);
    }
}
