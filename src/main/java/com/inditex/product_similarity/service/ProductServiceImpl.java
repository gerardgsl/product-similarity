package com.inditex.product_similarity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inditex.product_similarity.client.ProductClient;
import com.inditex.product_similarity.model.Product;
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductClient productClient;

    public ProductServiceImpl(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public List<Product> getSimilarProducts(String productId) {
        List<String> ids = productClient.getSimilarProductIds(productId);
        return ids.stream()
                  .map(productClient::getProductById)
                  .collect(Collectors.toList());
    }
}
