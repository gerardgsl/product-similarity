package com.inditex.product_similarity.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.inditex.product_similarity.exception.ProductNotFoundException;
import com.inditex.product_similarity.model.Product;

@Component
public class ProductClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:3001/product/";

    public List<String> getSimilarProductIds(String productId) {
        ResponseEntity<String[]> response = restTemplate.getForEntity(BASE_URL + productId + "/similarids", String[].class);
        return Arrays.asList(response.getBody());
    }

    public Product getProductById(String id) {
        try {
            return restTemplate.getForObject(BASE_URL + id, Product.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Product " + id + " not found");
        }
    }
}

