package com.inditex.product_similarity.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.inditex.dto.ProductDetail;
import com.inditex.product_similarity.client.ProductClient;
import com.inditex.product_similarity.exception.ProductNotFoundException;
import com.inditex.product_similarity.mapper.ProductMapper;
import com.inditex.product_similarity.model.Product;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductClient productClient;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductClient client, ProductMapper productMapper) {
        this.productClient = client;
        this.productMapper = productMapper;
    }

    @Override
    @Cacheable("similarProducts")
    public List<ProductDetail> getSimilarProducts(String productId) {
        try {
            productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("");
        }
        
        List<String> ids = productClient.getSimilarProductIds(productId);
        return ids.parallelStream()
                .map(this::getProductSafe)
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    @Retry(name = "product-api", fallbackMethod = "fallbackProduct")
    @CircuitBreaker(name = "product-api", fallbackMethod = "fallbackProduct")
    @TimeLimiter(name = "product-api")
    public CompletableFuture<ProductDetail> getProductAsync(String id) {
        // Ejecutamos la llamada Feign en un hilo aparte
        return CompletableFuture.supplyAsync(() -> {
            Product product = productClient.getProductById(id);
            return productMapper.toDto(product);
        });
    }

    /**
     * Adaptador síncrono: espera el resultado de getProductAsync o null si falla.
     */
    private ProductDetail getProductSafe(String id) {
        try {
            return getProductAsync(id).join(); // join() propaga excepciones como unchecked
        } catch (Exception ex) {
            return null; // seguridad extra (no debería llegar)
        }
    }

    /* ---------- Fallbacks ---------- */

    @SuppressWarnings("unused")
    private CompletableFuture<ProductDetail> fallbackProduct(String id, Throwable t) {
        // Aquí podrías loggear el error o enviar métricas
        return CompletableFuture.completedFuture(null);
    }
}
