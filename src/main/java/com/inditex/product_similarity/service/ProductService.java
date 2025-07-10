package com.inditex.product_similarity.service;

import java.util.List;
import com.inditex.product_similarity.model.Product;

public interface ProductService {
    List<Product> getSimilarProducts(String productId);
}
