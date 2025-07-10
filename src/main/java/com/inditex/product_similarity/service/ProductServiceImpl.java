package com.inditex.product_similarity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inditex.product_similarity.client.ProductClient;
import com.inditex.product_similarity.model.Product;
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductClient client;

    public ProductServiceImpl(ProductClient client) {
        this.client = client;
    }

    public List<Product> getSimilarProducts(String productId) {
        List<String> ids = client.getSimilarProductIds(productId);
        return ids.stream()
                  .map(client::getProductById)
                  .collect(Collectors.toList());
    }
}
