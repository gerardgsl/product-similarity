package com.inditex.product_similarity.service;

import java.util.List;
import java.util.Set;

import com.inditex.dto.ProductDetail;
import com.inditex.product_similarity.model.Product;

public interface ProductService {
    List<ProductDetail> getSimilarProducts(String productId);
}
