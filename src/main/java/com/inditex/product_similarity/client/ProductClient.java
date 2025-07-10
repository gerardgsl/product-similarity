package com.inditex.product_similarity.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.inditex.product_similarity.exception.ProductNotFoundException;
import com.inditex.product_similarity.model.Product;

@FeignClient(name = "productClient", url = "http://localhost:3001")
public interface ProductClient {
    @GetMapping("/product/{productId}/similarids")
    List<String> getSimilarProductIds(@PathVariable String productId);

    @GetMapping("/product/{productId}")
    Product getProductById(@PathVariable String productId);
}

