package com.inditex.product_similarity.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.inditex.product_similarity.model.Product;

@FeignClient(name = "productClient", url = "http://localhost:3001")
public interface ProductClient {
    @GetMapping("/product/{productId}/similarids")
    List<String> getSimilarProductIds(@PathVariable String productId);

    @GetMapping("/product/{productId}")
    Product getProductById(@PathVariable String productId);
}

